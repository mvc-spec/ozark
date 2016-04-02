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

import javax.mvc.Models;
import javax.mvc.engine.ViewEngineContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.UriInfo;

/**
 * Implementation of {@link javax.mvc.engine.ViewEngineContext}. Provides all the information
 * needed for a view engine to process a view.
 *
 * @author Santiago Pericas-Geertsen
 * @author Ivar Grimstad
 */
public class ViewEngineContextImpl implements ViewEngineContext {

    private final String view;

    private final Models models;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final UriInfo uriInfo;

    private final ResourceInfo resourceInfo;

    private final Configuration configuration;

    /**
     * Constructor for view engine contexts.
     *
     * @param view Name of view.
     * @param models Instance of models.
     * @param request HTTP servlet request.
     * @param response HTTP servlet response.
     * @param uriInfo URI info about the request.
     * @param resourceInfo Resource matched info.
     * @param configuration the configuration.
     */
    public ViewEngineContextImpl(String view, Models models, HttpServletRequest request, HttpServletResponse response,
                                 UriInfo uriInfo, ResourceInfo resourceInfo, Configuration configuration) {
        this.view = view;
        this.models = models;
        this.request = request;
        this.response = response;
        this.uriInfo = uriInfo;
        this.resourceInfo = resourceInfo;
        this.configuration = configuration;
    }

    @Override
    public String getView() {
        return view;
    }

    @Override
    public Models getModels() {
        return models;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @Override
    public ResourceInfo getResourceInfo() {
        return resourceInfo;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
