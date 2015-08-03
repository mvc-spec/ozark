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

import org.glassfish.ozark.core.Messages;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.mvc.annotation.CsrfValid;
import javax.mvc.security.Csrf;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;

import static org.glassfish.ozark.util.AnnotationUtils.hasAnnotation;

/**
 * <p>Reader interceptor that checks for the CSRF header and token. If not available as
 * an HTTP header, it looks for it as a form parameter in which case the media type must be
 * {@link javax.ws.rs.core.MediaType#APPLICATION_FORM_URLENCODED_TYPE}. If validation
 * fails, a 403 error is returned.
 * <p/>
 * <p>Because this interceptor is bound by name and not globally, it does not check
 * the HTTP method (note that CSRF validation should only apply to non-idempotent
 * requests).</p>
 * <p/>
 * <p>Stream buffering is required to restore the entity for the next interceptor.
 * If validation succeeds, it calls the next interceptor in the chain. Default
 * character encoding is utf-8. Even though none of the main browsers send a
 * charset param on a form post, we still check it to decode the entity.</p>
 *
 * @author Santiago Pericas-Geertsen
 * @see <a href="http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">HTML 4.0 Appendix</a>
 */
@Priority(Priorities.HEADER_DECORATOR)
public class CsrfValidateInterceptor implements ReaderInterceptor {

    private static final int BUFFER_SIZE = 4096;
    private static final String DEFAULT_CHARSET = "UTF-8";

    @Inject
    private Csrf csrf;

    @Context
    private Configuration config;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private Messages messages;

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        // Validate if name bound or if CSRF property enabled and a POST
        final Method controller = resourceInfo.getResourceMethod();
        if (needsValidation(controller)) {
            // First check if CSRF token is in header
            final String csrfHeader = csrf.getName();
            final String csrfToken = context.getHeaders().getFirst(csrfHeader);
            if (csrf.getToken().equals(csrfToken)) {
                return context.proceed();
            }

            // Otherwise, it must be a form parameter
            final MediaType contentType = context.getMediaType();
            if (!contentType.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
                throw new ForbiddenException(messages.get("UnableValidateCsrf", context.getMediaType()));
            }

            // Ensure stream can be restored for next interceptor
            ByteArrayInputStream bais;
            final InputStream is = context.getInputStream();
            if (is instanceof ByteArrayInputStream) {
                bais = (ByteArrayInputStream) is;
            } else {
                bais = copyStream(is);
            }

            // Validate CSRF
            boolean validated = false;
            final String charset = contentType.getParameters().get("charset");
            final String entity = toString(bais, charset != null ? charset : DEFAULT_CHARSET);
            final String[] pairs = entity.split("\\&");
            for (int i = 0; i < pairs.length; i++) {
                final String[] fields = pairs[i].split("=");
                final String nn = URLDecoder.decode(fields[0], DEFAULT_CHARSET);
                // Is this the CSRF field?
                if (csrf.getName().equals(nn)) {
                    final String vv = URLDecoder.decode(fields[1], DEFAULT_CHARSET);
                    // If so then check the token
                    if (csrf.getToken().equals(vv)) {
                        validated = true;
                        break;
                    }
                    throw new ForbiddenException(messages.get("CsrfFailed", "mismatching tokens"));
                }
            }
            if (!validated) {
                throw new ForbiddenException(messages.get("CsrfFailed", "missing field"));
            }

            // Restore stream and proceed
            bais.reset();
            context.setInputStream(bais);
        }
        return context.proceed();
    }

    private ByteArrayInputStream copyStream(InputStream is) throws IOException {
        int n;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            while ((n = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

    private String toString(ByteArrayInputStream bais, String encoding) throws UnsupportedEncodingException {
        int n = 0;
        final byte[] bb = new byte[bais.available()];
        while ((n = bais.read(bb, n, bb.length - n)) >= 0) ;
        bais.reset();
        return new String(bb, encoding);
    }

    /**
     * Determines if a controller method needs CSRF validation based on the config options.
     *
     * @param controller controller to inspect.
     * @return outcome of test.
     */
    private boolean needsValidation(Method controller) {
        if (controller == null || !hasAnnotation(controller, POST.class)) {
            return false;
        }
        final Object value = config.getProperty(Csrf.CSRF_PROTECTION);
        if (value != null) {
            final Csrf.CsrfOptions options = (Csrf.CsrfOptions) config.getProperty(Csrf.CSRF_PROTECTION);
            switch (options) {
                case OFF:
                    return false;
                case IMPLICIT:
                    return true;
                case EXPLICIT:
                    return hasAnnotation(controller, CsrfValid.class);
            }
        }
        return false;
    }
}
