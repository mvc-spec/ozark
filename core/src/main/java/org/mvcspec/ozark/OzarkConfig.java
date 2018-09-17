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
package org.mvcspec.ozark;

import org.mvcspec.ozark.jaxrs.JaxRsContext;
import org.mvcspec.ozark.security.CsrfTokenStrategy;
import org.mvcspec.ozark.security.SessionCsrfTokenStrategy;

import javax.inject.Inject;
import javax.mvc.security.Csrf;
import javax.ws.rs.core.Configuration;

/**
 * This class encapsulates the effective runtime configuration. All methods
 * will return either the explicitly configured configuration value or the default
 * value.
 *
 * @author Christian Kaltepoth
 */
public class OzarkConfig {

    @Inject
    @JaxRsContext
    private Configuration config;

    public Csrf.CsrfOptions getCsrfOptions() {

        // check for the config property
        final Object value = config.getProperty(Csrf.CSRF_PROTECTION);
        if (value instanceof Csrf.CsrfOptions) {
            return (Csrf.CsrfOptions) value;
        }

        // default as defined in the spec
        return Csrf.CsrfOptions.EXPLICIT;

    }

    public CsrfTokenStrategy getCsrfTokenStrategy() {

        Object value = config.getProperty(Properties.CSRF_TOKEN_STRATEGY);
        if (value instanceof CsrfTokenStrategy) {
            return (CsrfTokenStrategy) value;
        }

        // default
        return new SessionCsrfTokenStrategy.Builder().build();

    }

    public String getDefaultViewFileExtension() {
        Object value = config.getProperty(Properties.DEFAULT_VIEW_FILE_EXTENSION);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

}
