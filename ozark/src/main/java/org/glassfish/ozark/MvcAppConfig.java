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
package org.glassfish.ozark;

import org.glassfish.ozark.servlet.OzarkContainerInitializer;
import org.glassfish.ozark.util.PathUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Configuration;
import java.util.logging.Logger;

/**
 * This application scoped class holds all the global configuration like the
 * context and application path and is used by {@link MvcContextImpl} to expose
 * this information to the user.
 */
@ApplicationScoped
public class MvcAppConfig {

    private static final Logger log = Logger.getLogger(MvcAppConfig.class.getName());

    private String contextPath;

    private String applicationPath;

    private Configuration config;

    @Inject
    private ServletContext servletContext;

    @PostConstruct
    public void init() {

        Object appPath = servletContext.getAttribute(OzarkContainerInitializer.APP_PATH_CONTEXT_KEY);
        if (appPath != null) {
            this.applicationPath = PathUtils.normalizePath(appPath.toString());
        } else {
            log.warning("Unable to detect application path. " +
                    "This means that ${mvc.applicationPath} and ${mvc.basePath} will not work correctly");
        }

    }
    
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;     // normalized by servlet
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }
}
