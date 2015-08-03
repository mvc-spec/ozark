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
package org.glassfish.ozark.security;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.security.Csrf;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * <p>Response filter that adds the CSRF header with a unique token value. When CSRF
 * is enabled, clients must submit this header or a form field of name
 * {@link javax.mvc.security.Csrf#getName()} with the same token value for validation
 * to succeed.</p>
 *
 * <p>CSRF can be enabled by setting the property {@link javax.mvc.security.Csrf#CSRF_PROTECTION}
 * to {@link javax.mvc.security.Csrf.CsrfOptions#IMPLICIT}, to by setting it to
 * {@link javax.mvc.security.Csrf.CsrfOptions#EXPLICIT} and annotating the desired
 * controllers with {@link javax.mvc.annotation.CsrfValid}. Note that validation only
 * applies to controllers also annotated by {@link javax.ws.rs.POST}.</p>
 *
 * @author Santiago Pericas-Geertsen
 */
@Priority(Priorities.HEADER_DECORATOR)
public class CsrfProtectFilter implements ContainerResponseFilter {

    @Inject
    private Instance<Csrf> csrfInstance;

    @Context
    private Configuration config;

    /**
     * Inject CSRF header if enabled in the application.
     *
     * @param requestContext the request context.
     * @param responseContext the response context.
     * @throws IOException if a problem occurs writing a response.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        if (isCsrfEnabled()) {
            final Csrf csrf = csrfInstance.get();
            final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            if (!headers.containsKey(csrf.getName())) {
                headers.putSingle(csrf.getName(), csrf.getToken());
            }
        }
    }

    /**
     * Determines if CSRF is enabled in the application.
     *
     * @return outcome of test.
     */
    private boolean isCsrfEnabled() {
        final Object value = config.getProperty(Csrf.CSRF_PROTECTION);
        return value != null ? ((Csrf.CsrfOptions) value) != Csrf.CsrfOptions.OFF : false;
    }
}
