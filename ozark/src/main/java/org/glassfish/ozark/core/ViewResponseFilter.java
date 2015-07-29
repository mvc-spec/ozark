/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.ozark.core;

import org.glassfish.ozark.util.CdiUtils;
import org.glassfish.ozark.event.ControllerMatched;
import org.glassfish.ozark.jersey.VariantSelector;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.View;
import javax.mvc.Viewable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.lang.reflect.Method;

import static javax.ws.rs.core.Response.Status.FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.glassfish.ozark.util.PathUtils.noStartingSlash;
import static org.glassfish.ozark.util.PathUtils.noPrefix;
import static org.glassfish.ozark.util.AnnotationUtils.getAnnotation;

/**
 * <p>A JAX-RS response filter that fires a {@link javax.mvc.event.ControllerMatched}
 * event. It also verifies the static return type of the controller method is correct,
 * and ensures that the entity is a {@link javax.mvc.Viewable} to be processed by
 * {@link org.glassfish.ozark.core.ViewableWriter}.</p>
 *
 * <p>A {@link org.glassfish.ozark.jersey.VariantSelector} implements the algorithm in
 * Section 3.8 of the JAX-RS specification to compute the final Content-Type when
 * the method returns void (no entity). If unable to compute the final Content-Type,
 * e.g. if the controller method is not annotated by {@code @Produces}, it defaults to
 * {@code text/html}. If the method does not return void (has an entity), the computation
 * of the Content-Type is done by JAX-RS and is available via {@code responseContext}.</p>
 *
 * <p>Given that this filter is annotated by {@link javax.mvc.Controller}, it will
 * be called after every controller method returns. Priority is set to
 * {@link javax.ws.rs.Priorities#ENTITY_CODER} which means it will be executed
 * after user-defined filters (response filters are sorted in reverse order).</p>
 *
 * @author Santiago Pericas-Geertsen
 */
@Controller
@Priority(Priorities.ENTITY_CODER)
public class ViewResponseFilter implements ContainerResponseFilter {

    private static final String REDIRECT = "redirect:";
    private static final String LOCATION_HEADER = "Location";

    @Context
    private UriInfo uriInfo;

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest request;

    @Inject
    private Event<ControllerMatched> matchedEvent;

    @Inject
    private ControllerMatched matched;

    @Inject
    private CdiUtils cdiUtils;

    @Inject
    private Messages messages;

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        // Fire ControllerMatched event
        matched.setUriInfo(uriInfo);
        matched.setResourceInfo(resourceInfo);
        matchedEvent.fire(matched);

        final Method method = resourceInfo.getResourceMethod();
        final Class<?> returnType = method.getReturnType();

        // Wrap entity type into Viewable, possibly looking at @View
        Object entity = responseContext.getEntity();
        final Class<?> entityType = entity != null ? entity.getClass() : null;
        if (entityType == null) {       // NO_CONTENT
            View an = method.getAnnotation(View.class);
            if (an == null) {
                an = getAnnotation(resourceInfo.getResourceClass(), View.class);
            }
            if (an != null) {
                MediaType contentType = VariantSelector.selectVariant(request, resourceInfo);
                if (contentType == null) {
                    contentType = MediaType.TEXT_HTML_TYPE;     // default
                }
                responseContext.setEntity(new Viewable(an.value()), null, contentType);
                responseContext.setStatusInfo(OK);      // Needed for method returning void
            } else if (returnType == Void.class) {
                throw new ServerErrorException(messages.get("VoidControllerNoView", resourceInfo.getResourceMethod()),
                    Response.Status.INTERNAL_SERVER_ERROR);
            }
        } else if (entityType != Viewable.class) {
            final String view = entity.toString();
            if (view == null) {
                throw new ServerErrorException(messages.get("EntityToStringNull", resourceInfo.getResourceMethod()),
                        Response.Status.INTERNAL_SERVER_ERROR);
            }
            responseContext.setEntity(new Viewable(view), null, responseContext.getMediaType());
        }

        // Redirect logic, entity must be a Viewable if not null
        entity = responseContext.getEntity();
        if (entity != null) {
            final String view = ((Viewable) entity).getView();
            if (view.startsWith(REDIRECT)) {
                final String uri = uriInfo.getBaseUri() + noStartingSlash(noPrefix(view, REDIRECT));
                responseContext.setStatusInfo(FOUND);
                responseContext.getHeaders().putSingle(LOCATION_HEADER, uri);
                responseContext.setEntity(null);
            }
        }
    }
}
