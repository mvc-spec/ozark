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
package org.mvcspec.ozark.servlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.ws.rs.ApplicationPath;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mvc.annotation.Controller;
import javax.ws.rs.Path;

import static org.mvcspec.ozark.util.AnnotationUtils.getAnnotation;
import static org.mvcspec.ozark.util.AnnotationUtils.hasAnnotationOnClassOrMethod;

/**
 * Initializes the Mvc class with the application and context path. Note that the
 * application path is only initialized if there is an application sub-class that
 * is annotated by {@link javax.ws.rs.ApplicationPath}.
 *
 * @author Santiago Pericas-Geertsen
 * @author Dmytro Maidaniuk
 */
@HandlesTypes({ ApplicationPath.class, Path.class })
public class OzarkContainerInitializer implements ServletContainerInitializer {

    public static final String APP_PATH_CONTEXT_KEY = OzarkContainerInitializer.class.getName() + ".APP_PATH";
    public static final String OZARK_ENABLE_FEATURES_KEY = "ozark.enableFeatures";
    private static final Logger LOG = Logger.getLogger(OzarkContainerInitializer.class.getName());

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        servletContext.setAttribute(OZARK_ENABLE_FEATURES_KEY, false);
        if (classes != null && !classes.isEmpty()) {
            LOG.log(Level.INFO, "Ozark version {0} started", getClass().getPackage().getImplementationVersion());
            for (Class<?> clazz : classes) {
                final ApplicationPath ap = getAnnotation(clazz, ApplicationPath.class);
                if (ap != null) {
                    if (servletContext.getAttribute(APP_PATH_CONTEXT_KEY) != null) {
                        // must be a singleton
                        throw new IllegalStateException("More than one JAX-RS ApplicationPath detected!");
                    }
                    servletContext.setAttribute(APP_PATH_CONTEXT_KEY, ap.value());
                }
                if (hasAnnotationOnClassOrMethod(clazz, Path.class)
                        && hasAnnotationOnClassOrMethod(clazz, Controller.class)) {
                    servletContext.setAttribute(OZARK_ENABLE_FEATURES_KEY, true);
                }
                if (servletContext.getAttribute(APP_PATH_CONTEXT_KEY) != null
                        && (Boolean) servletContext.getAttribute(OZARK_ENABLE_FEATURES_KEY) == true) {
                    break;  // no need to loop further
                }
            }
        }
    }
}
