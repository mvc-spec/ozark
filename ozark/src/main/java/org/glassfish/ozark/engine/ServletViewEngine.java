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
package org.glassfish.ozark.engine;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.engine.ViewEngineContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Base class for servlet-based view engines like JSPs and Facelets. Implements
 * a forward mechanism that attempts to avoid the standard servlet matching by
 * first looking at servlets that handle the specified extensions directly.
 *
 * @author Santiago Pericas-Geertsen
 * @author Ivar Grimstad
 */
public abstract class ServletViewEngine extends ViewEngineBase {

    @Inject
    protected ServletContext servletContext;

    /**
     * <p>Forwards request to servlet container. Search for a servlet by matching
     * the supplied extensions; if that fails, execute a normal forward via
     * servlet matching.</p>
     *
     * <p>Note that if the MVC application overrides the root context by setting the
     * application path to "/" or "/*", then a forward will result in an infinite
     * recursion because the servlet container will forward the request back to MVC
     * (or JAX-RS). Thus, it is important to try to find the servlet using extensions
     * first instead of matching.</p>
     *
     * @param context view engine context.
     * @param extensions list of extensions that need to match.
     * @throws ServletException if there is an error with the forward.
     * @throws IOException if there is an I/O error.
     */
    protected void forwardRequest(ViewEngineContext context, String... extensions)
            throws ServletException, IOException {
        RequestDispatcher rd = null;
        final OzarkViewEngineContext viewEngineContext = (OzarkViewEngineContext) context;
        HttpServletRequest request = viewEngineContext.getRequest();
        final HttpServletResponse response = viewEngineContext.getResponse();

        // Set attributes in request before forward
        final Models models = context.getModels();
        for (String name : models) {
            request.setAttribute(name, models.get(name));
        }

        // Find request dispatcher based on extensions
        for (Map.Entry<String, ? extends ServletRegistration> e : servletContext.getServletRegistrations().entrySet()) {
            final Collection<String> mappings = e.getValue().getMappings();
            if (mappings.containsAll(Arrays.asList(extensions))) {
                rd = servletContext.getNamedDispatcher(e.getKey());     // by servlet name

                // Need new request with updated URI and extension matching semantics
                request = new HttpServletRequestWrapper(viewEngineContext.getRequest()) {
                    @Override
                    public String getRequestURI() {
                        return resolveView(context);
                    }

                    @Override
                    public String getServletPath() {
                        return resolveView(context);
                    }

                    @Override
                    public String getPathInfo() {
                        return null;
                    }

                    @Override
                    public StringBuffer getRequestURL() {
                        return new StringBuffer(getRequestURI());
                    }
                };
                break;
            }
        }

        // If none found, go through servlet mapping
        if (rd == null) {
            rd = servletContext.getRequestDispatcher(resolveView(context));
        }

        // Forward request to servlet
        rd.forward(request, response);
    }
}
