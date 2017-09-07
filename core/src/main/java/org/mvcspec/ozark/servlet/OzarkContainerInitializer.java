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

import static org.mvcspec.ozark.util.AnnotationUtils.getAnnotation;

/**
 * Initializes the Mvc class with the application and context path. Note that the
 * application path is only initialized if there is an application sub-class that
 * is annotated by {@link javax.ws.rs.ApplicationPath}.
 *
 * @author Santiago Pericas-Geertsen
 */
@HandlesTypes({ ApplicationPath.class })
public class OzarkContainerInitializer implements ServletContainerInitializer {

    public static final String APP_PATH_CONTEXT_KEY = OzarkContainerInitializer.class.getName() + ".APP_PATH";

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        if (classes != null && !classes.isEmpty()) {
            final Class<?> appClass = classes.iterator().next();    // must be a singleton
            final ApplicationPath ap = getAnnotation(appClass, ApplicationPath.class);
            if (ap != null) {
                servletContext.setAttribute(APP_PATH_CONTEXT_KEY, ap.value());
            }
        }
    }
}
