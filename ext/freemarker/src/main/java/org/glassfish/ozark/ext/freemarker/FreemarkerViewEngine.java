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
package org.glassfish.ozark.ext.freemarker;

import org.glassfish.ozark.engine.ViewEngineBase;
import org.glassfish.ozark.engine.ViewEngineConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Class FreemarkerViewEngine.
 *
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
public class FreemarkerViewEngine extends ViewEngineBase {

    @Inject
    @ViewEngineConfig
    private Configuration configuration;

    @Override
    public boolean supports(String view) {
        return view.endsWith(".ftl");
    }

    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {
        try {
            final Template template = configuration.getTemplate(resolveView(context));
            template.process(context.getModels(),
                    new OutputStreamWriter(context.getResponse().getOutputStream()));
        } catch (TemplateException | IOException e) {
            throw new ViewEngineException(e);
        }
    }
}
