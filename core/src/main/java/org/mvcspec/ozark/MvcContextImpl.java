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
import org.mvcspec.ozark.servlet.OzarkContainerInitializer;
import org.mvcspec.ozark.uri.ApplicationUris;
import org.mvcspec.ozark.util.PathUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.MvcContext;
import javax.mvc.MvcUriBuilder;
import javax.mvc.security.Csrf;
import javax.mvc.security.Encoders;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Configuration;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Implementation of {@link javax.mvc.MvcContext}.
 *
 * @author Santiago Pericas-Geertsen
 */
@Named("mvc")
@RequestScoped
public class MvcContextImpl implements MvcContext {

    private static final Logger log = Logger.getLogger(MvcContextImpl.class.getName());

    @Inject
    private Csrf csrf;

    @Inject
    private Encoders encoders;

    @Inject
    private ServletContext servletContext;

    @Inject
    private ApplicationUris applicationUris;

    @Inject
    @JaxRsContext
    private Configuration configuration;

    private Locale locale;

    private String applicationPath;

    @PostConstruct
    public void init() {

        Object appPath = servletContext.getAttribute(OzarkContainerInitializer.APP_PATH_CONTEXT_KEY);
        if (appPath != null) {
            this.applicationPath = PathUtils.normalizePath(appPath.toString());
        } else {
            log.warning("Unable to detect application path. " +
                    "This means that ${mvc.applicationPath} and ${mvc.basePath} will not work correctly");
        }

        if (configuration == null) {
            throw new IllegalArgumentException("Cannot obtain JAX-RS Configuration instance");
        }

    }

    @Override
    public String getContextPath() {
        return servletContext.getContextPath();   // normalized by servlet
    }

    @Override
    public String getApplicationPath() {
        return applicationPath;
    }

    @Override
    public String getBasePath() {
        if (getApplicationPath() != null) {
            return getContextPath() + getApplicationPath();
        }
        return getContextPath();
    }

    @Override
    public Csrf getCsrf() {
        return csrf;
    }

    @Override
    public Encoders getEncoders() {
        return encoders;
    }

    @Override
    public Configuration getConfig() {
        return configuration;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public URI uri(String identifier) {
        return applicationUris.get(identifier);
    }

    @Override
    public URI uri(String identifier, Map<String, Object> params) {
        return applicationUris.get(identifier, params);
    }

    @Override
    public MvcUriBuilder uriBuilder(String identifier) {
        return applicationUris.getUriBuilder(identifier);
    }

}
