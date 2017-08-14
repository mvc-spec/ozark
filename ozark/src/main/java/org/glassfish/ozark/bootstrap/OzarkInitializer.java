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
package org.glassfish.ozark.bootstrap;

import org.glassfish.ozark.core.ViewResponseFilter;
import org.glassfish.ozark.util.AnnotationUtils;

import javax.mvc.annotation.Controller;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.FeatureContext;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Main class for triggering initialization of Ozark
 *
 * @author Christian Kaltepoth
 */
public class OzarkInitializer {

    private static final Logger log = Logger.getLogger(OzarkInitializer.class.getName());

    /**
     * Registers all required provides for Ozark. Please note that the initialization is
     * only performed if at least one controller is detected for the application and if the
     * initialization hasn't been triggered before. So calling this method multiple times
     * won't result in duplicated providers registered.
     */
    public static void initialize(FeatureContext context) {

        Configuration config = context.getConfiguration();

        if (!isAlreadyInitialized(config) && isMvcApplication(config)) {

            log.info("Initializing Ozark...");

            for (ConfigProvider provider : ServiceLoader.load(ConfigProvider.class)) {
                log.fine("Executing: " + provider.getClass().getName());
                provider.configure(context);
            }

        }

    }

    private static boolean isAlreadyInitialized(Configuration config) {
        return config.isRegistered(ViewResponseFilter.class);
    }

    private static boolean isMvcApplication(Configuration config) {
        return config.getClasses().stream().anyMatch(OzarkInitializer::isController)
            || config.getInstances().stream().map(o -> o.getClass()).anyMatch(OzarkInitializer::isController);
    }

    private static boolean isController(Class<?> c) {

        if (AnnotationUtils.getAnnotation(c, Controller.class) != null) {
            return true;
        }

        if (Arrays.stream(c.getMethods()).anyMatch(m -> AnnotationUtils.getAnnotation(m, Controller.class) != null)) {
            return true;
        }

        return false;

    }

}
