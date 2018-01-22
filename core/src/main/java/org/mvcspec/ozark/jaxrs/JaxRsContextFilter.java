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
package org.mvcspec.ozark.jaxrs;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import java.io.IOException;
import javax.ws.rs.core.Application;

/**
 * This filter is used to get the JAX-RS context objects and feed them to the corresponding
 * CDI producer.
 *
 * @author Christian Kaltepoth
 */
@PreMatching
@Priority(0) // very early
public class JaxRsContextFilter implements ContainerRequestFilter {

    @Inject
    private JaxRsContextProducer jaxRsContextProducer;

    @Context
    private Configuration configuration;

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    @Context
    private Application application;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        jaxRsContextProducer.populate(configuration, request, response, application);
    }

}
