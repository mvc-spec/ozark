/*
 * Copyright © 2017 Ivar Grimstad (ivar.grimstad@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mvcspec.ozark.engine;

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

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
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
