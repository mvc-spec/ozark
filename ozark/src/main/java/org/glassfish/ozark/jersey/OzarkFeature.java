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
package org.glassfish.ozark.jersey;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;
import org.glassfish.ozark.core.ViewRequestFilter;
import org.glassfish.ozark.core.ViewResponseFilter;
import org.glassfish.ozark.core.ViewableWriter;
import org.glassfish.ozark.locale.LocaleRequestFilter;
import org.glassfish.ozark.security.CsrfProtectFilter;
import org.glassfish.ozark.security.CsrfValidateInterceptor;

import javax.annotation.Priority;
import javax.mvc.annotation.Controller;
import javax.servlet.ServletContext;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import java.util.Arrays;

import static org.glassfish.ozark.util.AnnotationUtils.getAnnotation;

/**
 * <p>Jersey feature that sets up the JAX-RS pipeline for MVC processing using one
 * or more providers. This feature is enabled only if any of the classes or methods
 * in the application has an instance of the {@link javax.mvc.annotation.Controller} annotation.</p>
 *
 * <p>Takes advantage of the {@link org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable}
 * SPI in Jersey.</p>
 *
 * @author Santiago Pericas-Geertsen
 * @author Eddú Meléndez
 */
@ConstrainedTo(RuntimeType.SERVER)
@Priority(AutoDiscoverable.DEFAULT_PRIORITY)
public class OzarkFeature implements ForcedAutoDiscoverable {

    @Context
    private ServletContext servletContext;

    @Override
    public void configure(FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (config.isRegistered(ViewResponseFilter.class)) {
            return;     // already registered!
        }
        final boolean enableOzark = config.getClasses().stream().anyMatch(this::isController)
                || config.getInstances().stream().map(o -> o.getClass()).anyMatch(this::isController);
        if (enableOzark) {
            context.register(ViewRequestFilter.class);
            context.register(ViewResponseFilter.class);
            context.register(ViewableWriter.class);
            context.register(OzarkModelProcessor.class);
            context.register(CsrfValidateInterceptor.class);
            context.register(CsrfProtectFilter.class);
            context.register(LocaleRequestFilter.class);
        }
    }

    private boolean isController(Class<?> c) {
        return getAnnotation(c, Controller.class) != null ||
                Arrays.stream(c.getMethods()).anyMatch(m -> getAnnotation(m, Controller.class) != null);
    }
}
