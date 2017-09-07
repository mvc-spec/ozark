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
package org.mvcspec.ozark.ext.velocity;

import org.mvcspec.ozark.engine.ViewEngineBase;
import org.mvcspec.ozark.engine.ViewEngineConfig;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import java.io.IOException;

/**
 * Class VelocityViewEngine.
 *
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class VelocityViewEngine extends ViewEngineBase {

    @Inject
    @ViewEngineConfig
    private VelocityEngine velocityEngine;

    @Override
    public boolean supports(String view) {
        return view.endsWith(".vm");
    }

    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {
        try {
            Template template = velocityEngine.getTemplate(resolveView(context));
            VelocityContext velocityContext = new VelocityContext(context.getModels());
            template.merge(velocityContext, context.getResponse().getWriter());
        } catch (IOException e) {
            throw new ViewEngineException(e);
        }
    }
}
