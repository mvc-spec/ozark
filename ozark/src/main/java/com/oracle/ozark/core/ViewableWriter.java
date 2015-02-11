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

import com.oracle.ozark.engine.ViewEngineContext;
import com.oracle.ozark.engine.ViewEngineFinder;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.Viewable;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

/**
 * Class ViewableWriter.
 *
 * @author Santiago Pericas-Geertsen
 */
@Produces(MediaType.TEXT_HTML)
public class ViewableWriter implements MessageBodyWriter<Viewable> {

    private static final String DEFAULT_ENCODING = "ISO-8859-1";        // HTTP 1.1

    @Inject
    private Instance<Models> modelsInstance;

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    @Context
    private UriInfo uriInfo;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private ViewEngineFinder engineFinder;

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == Viewable.class;
    }

    @Override
    public long getSize(Viewable viewable, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Viewable viewable, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> headers, OutputStream out)
            throws IOException, WebApplicationException {
        // Find engine for this Viewable
        final ViewEngine engine = engineFinder.find(viewable);
        if (engine == null) {
            RequestDispatcher requestDispatcher = 
                    request.getServletContext().getRequestDispatcher(viewable.getView());
            if (requestDispatcher != null) {
                try {
                    requestDispatcher.forward(request, response);
                } catch (ServletException ex) {
                    throw new IOException(ex);
                }
            }
            else {
                throw new WebApplicationException("Unable to find suitable view engine for '" + viewable + "'");
            }
        }
        
        // Create wrapper for response
        final ServletOutputStream responseStream = new ServletOutputStream() {
            @Override
            public void write(final int b) throws IOException {
                out.write(b);
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                throw new UnsupportedOperationException("Not supported");
            }
        };
        final PrintWriter responseWriter = new PrintWriter(new OutputStreamWriter(responseStream,
                findEncoding(headers, annotations)));
        final HttpServletResponse responseWrapper = new HttpServletResponseWrapper(response) {

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return responseStream;
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return responseWriter;
            }
        };

        // Pass request to view engine
        try {
            // If no models in viewable, inject via CDI
            Models models = viewable.getModels();
            if (models == null) {
                models = modelsInstance.get();
            }
            // Process view using selected engine
            engine.processView(new ViewEngineContext(viewable.getView(), models, request, responseWrapper,
                    uriInfo, resourceInfo));
        } catch (ViewEngineException e) {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR, e);
        } finally {
            responseWriter.flush();
        }
    }

    /**
     * Looks for a character set as part of the Content-Type header. Returns it
     * if specified or {@link DEFAULT_ENCODING} if not.
     * <p/>
     * TODO: Jersey does not seem to copy MIME type params from @Produces.
     * Thus, if a charset is specified in @Produces, it will not be available
     * in the Content-Type header.
     *
     * @param headers Response headers.
     * @return Character set to use.
     */
    private String findEncoding(MultivaluedMap<String, Object> headers, Annotation[] annotations) {
        final MediaType mt = (MediaType) headers.get("Content-Type").get(0);
        final String charset = mt.getParameters().get("charset");
        return charset != null ? charset : DEFAULT_ENCODING;
    }
}
