/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2015 Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.ozark.security;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.security.Csrf;
import javax.mvc.security.CsrfProtected;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.lang.reflect.Method;

import static java.lang.Boolean.TRUE;

/**
 * <p>Response filter that adds the CSRF header with a unique token value. Clients must
 * submit the header in subsequent form posts if validation is enabled on the controller
 * via {@link javax.mvc.security.CsrfValidated}. Alternatively, it is also possible to
 * inject hidden form fields returned by controllers, in which case the client does not
 * need any further processing before submitting a form.</p>
 * <p/>
 * <p>This filter is enabled only when either the annotation
 * {@link javax.mvc.security.CsrfProtected} decorates the matched controller or when
 * the global property {@link javax.mvc.security.Csrf#ENABLE_CSRF} is set to true
 * (defaults to false).</p>
 * <p/>
 * <p>Note that the CSRF header is added only if it is not already present in the
 * response.</p>
 *
 * @author Santiago Pericas-Geertsen
 */
@Priority(Priorities.HEADER_DECORATOR)
public class CsrfProtectFilter implements ContainerResponseFilter {

    @Inject
    private Instance<Csrf> csrfInstance;

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private Configuration config;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        if (TRUE.equals(config.getProperty(Csrf.ENABLE_CSRF)) || isNameBound(resourceInfo.getResourceMethod())) {
            final Csrf csrf = csrfInstance.get();
            final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            if (!headers.containsKey(csrf.getName())) {
                headers.putSingle(csrf.getName(), csrf.getToken());
            }
        }
    }

    private static boolean isNameBound(Method controller) {
        return (controller == null) ? false : controller.getAnnotation(CsrfProtected.class) != null;
    }
}
