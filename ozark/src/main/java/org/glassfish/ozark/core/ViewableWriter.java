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
package org.glassfish.ozark.core;

import org.glassfish.ozark.engine.ViewEngineContextImpl;
import org.glassfish.ozark.engine.ViewEngineFinder;
import org.glassfish.ozark.event.AfterProcessViewEventImpl;
import org.glassfish.ozark.event.BeforeProcessViewEventImpl;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.MvcContext;
import javax.mvc.Viewable;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineException;
import javax.mvc.event.AfterProcessViewEvent;
import javax.mvc.event.BeforeProcessViewEvent;
import javax.mvc.event.MvcEvent;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.glassfish.ozark.cdi.OzarkCdiExtension.isEventObserved;
import static org.glassfish.ozark.util.PathUtils.ensureStartingSlash;

/**
 * <p>Body writer for a {@link javax.mvc.Viewable} instance. Looks for a
 * {@link javax.mvc.engine.ViewEngine} that is capable of processing the view. If no
 * engine is found, it forwards the request back to the servlet container.</p>
 *
 * <p>If {@link javax.mvc.Models} is available in the viewable, it is used; otherwise,
 * this class is injected via CDI. A view engine in the viewable can also bypass
 * the lookup mechanism.</p>
 *
 * <p>The charset for the response is obtained from the media type, and defaults to
 * UTF-8.</p>
 *
 * @author Santiago Pericas-Geertsen
 */
@Produces(MediaType.WILDCARD)
public class ViewableWriter implements MessageBodyWriter<Viewable> {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final Charset UTF8 = Charset.forName("UTF-8");

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

    @Context
    private Configuration config;

    @Inject
    private Messages messages;

    @Inject
    private Event<MvcEvent> dispatcher;

    @Inject
    private MvcContext mvc;

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == Viewable.class;
    }

    @Override
    public long getSize(Viewable viewable, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * Searches for a suitable {@link javax.mvc.engine.ViewEngine} to process the view. If no engine
     * is found, is forwards the request back to the servlet container.
     */
    @Override
    public void writeTo(Viewable viewable, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> headers, OutputStream out)
            throws IOException, WebApplicationException {
        // Find engine for this Viewable
        final ViewEngine engine = engineFinder.find(viewable);
        if (engine == null) {
            RequestDispatcher requestDispatcher =
                    request.getServletContext().getRequestDispatcher(ensureStartingSlash(viewable.getView()));
            if (requestDispatcher != null) {
                try {
                    requestDispatcher.forward(request, response);
                } catch (ServletException ex) {
                    throw new ServerErrorException(INTERNAL_SERVER_ERROR, ex);
                }
            }
            else {
                throw new ServerErrorException(messages.get("NoViewEngine", viewable), INTERNAL_SERVER_ERROR);
            }
            return;     // null engine, can't proceed
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
        final PrintWriter responseWriter = new PrintWriter(new OutputStreamWriter(responseStream, getCharset(headers)));
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

            // Bind EL 'mvc' object in models
            models.put("mvc", mvc);

            // Fire BeforeProcessView event
            if (isEventObserved(BeforeProcessViewEvent.class)) {
                final BeforeProcessViewEventImpl event = new BeforeProcessViewEventImpl();
                event.setEngine(engine.getClass());
                event.setView(viewable.getView());
                dispatcher.fire(event);
            }

            // Process view using selected engine
            engine.processView(new ViewEngineContextImpl(viewable.getView(), models, request, responseWrapper,
                    uriInfo, resourceInfo, config));

            // Fire AfterProcessView event
            if (isEventObserved(AfterProcessViewEvent.class)) {
                final AfterProcessViewEventImpl event = new AfterProcessViewEventImpl();
                event.setEngine(engine.getClass());
                event.setView(viewable.getView());
                dispatcher.fire(event);
            }
        } catch (ViewEngineException e) {
            throw new ServerErrorException(INTERNAL_SERVER_ERROR, e);
        } finally {
            responseWriter.flush();
        }
    }

    /**
     * Looks for a character set as part of the Content-Type header. Returns it
     * if specified or {@link #UTF8} if not.
     *
     * @param headers Response headers.
     * @return Character set to use.
     */
    private Charset getCharset(MultivaluedMap<String, Object> headers) {
        final MediaType mt = (MediaType) headers.get(CONTENT_TYPE).get(0);
        final String charset = mt.getParameters().get(MediaType.CHARSET_PARAMETER);
        return charset != null ? Charset.forName(charset) : UTF8;
    }
}
