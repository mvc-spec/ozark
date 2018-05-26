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
package org.mvcspec.ozark.bootstrap;

import org.mvcspec.ozark.core.ViewResponseFilter;
import org.mvcspec.ozark.servlet.OzarkContainerInitializer;
import org.mvcspec.ozark.util.ServiceLoaders;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.FeatureContext;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for triggering initialization of Ozark
 *
 * @author Christian Kaltepoth
 */
public final class OzarkInitializer {

    private static final Logger log = Logger.getLogger(OzarkInitializer.class.getName());

    private OzarkInitializer() {
        // static methods only
    }

    /**
     * Registers all required provides for Ozark. Please note that the initialization is
     * only performed if at least one controller is detected for the application and if the
     * initialization hasn't been triggered before. So calling this method multiple times
     * won't result in duplicated providers registered.
     */
    public static void initialize(FeatureContext context, ServletContext servletContext) {

        Objects.requireNonNull(context, "FeatureContext is required");

        Configuration config = context.getConfiguration();

        if (!isAlreadyInitialized(config) && isMvcApplication(servletContext)) {

            log.info("Initializing Ozark...");

            for (ConfigProvider provider : ServiceLoaders.list(ConfigProvider.class)) {
                log.log(Level.FINE, "Executing: {0}", provider.getClass().getName());
                provider.configure(context);
            }

        }

    }

    private static boolean isAlreadyInitialized(Configuration config) {
        return config.isRegistered(ViewResponseFilter.class);
    }

    /**
     * Note: ServletContext may be null here, because CXF doesn't inject it correctly
     */
    private static boolean isMvcApplication(ServletContext servletContext) {

        // we fall back to enable Ozark if detection didn't work
        Set<Class<?>> controllersFound = servletContext != null
                ? (Set<Class<?>>) servletContext.getAttribute(OzarkContainerInitializer.CONTROLLER_CLASSES)
                : null;
        boolean enableOzark = controllersFound == null || !controllersFound.isEmpty();

        log.log(Level.FINE, "Is Ozark application detected: {0}", enableOzark);
        return enableOzark;

    }

}
