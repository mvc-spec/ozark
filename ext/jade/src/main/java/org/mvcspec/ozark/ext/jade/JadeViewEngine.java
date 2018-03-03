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
package org.mvcspec.ozark.ext.jade;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.exceptions.JadeException;
import org.mvcspec.ozark.engine.ViewEngineBase;
import org.mvcspec.ozark.engine.ViewEngineConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * The Jade View Engine.
 *
 * @author Florian Hirsch
 * @see <a href="http://jade-lang.com/">Jade</a>
 * @see <a href="https://github.com/neuland/jade4j">Jade4J</a>
 */
@ApplicationScoped
public class JadeViewEngine extends ViewEngineBase {

    @Inject
    @ViewEngineConfig
    private JadeConfiguration jade;

    @Override
    public boolean supports(String view) {
        return view.endsWith(".jade");
    }

    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {
        String viewPath = resolveView(context);
        Charset charset = resolveCharsetAndSetContentType(context);
        try (Writer writer = new OutputStreamWriter(context.getOutputStream(), charset)) {
            jade.renderTemplate(jade.getTemplate(viewPath), context.getModels(), writer);
        } catch (JadeException | IOException ex) {
            throw new ViewEngineException(String.format("Could not process view %s.", context.getView()), ex);
        }
    }
}
