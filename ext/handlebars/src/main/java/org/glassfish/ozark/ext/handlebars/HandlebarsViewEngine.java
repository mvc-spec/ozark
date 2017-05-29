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
package org.glassfish.ozark.ext.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.glassfish.ozark.engine.ViewEngineBase;
import org.glassfish.ozark.engine.ViewEngineConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.stream.Collectors;

/**
 * Class HandlebarsViewEngine
 *
 * @author Rahman Usta
 */
@ApplicationScoped
public class HandlebarsViewEngine extends ViewEngineBase {

    @Inject
    private ServletContext servletContext;

    @Inject
    @ViewEngineConfig
    private Handlebars handlebars;

    @Override
    public boolean supports(String view) {
        return view.endsWith(".hbs") || view.endsWith(".handlebars");
    }

    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {
        Models models = context.getModels();
        String viewName = context.getView();

        try (PrintWriter writer = context.getResponse().getWriter();
            InputStream resourceAsStream = servletContext.getResourceAsStream(resolveView(context));
            InputStreamReader in = new InputStreamReader(resourceAsStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(in);) {

            String viewContent = bufferedReader.lines().collect(Collectors.joining());

            Template template = handlebars.compileInline(viewContent);
            template.apply(models, writer);
        } catch (IOException e) {
            throw new ViewEngineException(e);
        }
    }
}
