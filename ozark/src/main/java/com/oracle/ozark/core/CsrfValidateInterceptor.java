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
package com.oracle.ozark.core;

import com.oracle.ozark.api.CsrfValidated;
import com.oracle.ozark.api.Mvc;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.*;
import java.net.URLDecoder;

/**
 * Class CsrfValidateRequestFilter.
 *
 * @author Santiago Pericas-Geertsen
 */
@CsrfValidated
@Priority(Priorities.HEADER_DECORATOR)
public class CsrfValidateInterceptor implements ReaderInterceptor {

    private static final String ENCODING = "UTF-8";

    @Inject
    private Mvc mvc;

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        ByteArrayInputStream bais;
        final InputStream is = context.getInputStream();
        if (is instanceof ByteArrayInputStream) {
            bais = (ByteArrayInputStream) is;
        } else {
            bais = copyStream(is);
        }

        boolean validated = false;
        final String encoded = toString(bais, ENCODING);
        final String[] pairs = encoded.split("\\&");
        for (int i = 0; i < pairs.length; i++) {
            final String[] fields = pairs[i].split("=");
            final String nn = URLDecoder.decode(fields[0], ENCODING);
            // Is this the CSRF field?
            if (mvc.getCsrfHeader().equals(nn)) {
                final String vv = URLDecoder.decode(fields[1], ENCODING);
                // If so then check the token
                if (mvc.getCsrfToken().equals(vv)) {
                    validated = true;
                    break;
                }
                throw new ForbiddenException("Validation of CSRF failed due to mismatching tokens");
            }
        }
        if (!validated) {
            throw new ForbiddenException("Validation of CSRF failed due to missing field");
        }

        bais.reset();
        context.setInputStream(bais);
        return context.proceed();
    }

    private ByteArrayInputStream copyStream(InputStream is) throws IOException {
        int n;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[2048];
            while ((n = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

    private String toString(ByteArrayInputStream bais, String encoding) throws UnsupportedEncodingException{
        int n = 0;
        final byte[] bb = new byte[bais.available()];
        while ((n = bais.read(bb, n, bb.length - n)) >= 0);
        bais.reset();
        return new String(bb, encoding);
    }
}
