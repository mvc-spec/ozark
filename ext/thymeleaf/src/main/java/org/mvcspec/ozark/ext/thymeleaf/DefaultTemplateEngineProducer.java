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
package org.mvcspec.ozark.ext.thymeleaf;

import org.mvcspec.ozark.engine.ViewEngineConfig;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 * Producer for the TemplateEngine used by ThymeleafViewEngine.
 *
 * @author Christian Kaltepoth
 * @author Eddú Meléndez
 */
public class DefaultTemplateEngineProducer {

    @Inject
    private ServletContext servletContext;

    @Produces
    @ViewEngineConfig
    public TemplateEngine getTemplateEngine() {

        ITemplateResolver resolver = new ServletContextTemplateResolver(this.servletContext);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;

    }

}
