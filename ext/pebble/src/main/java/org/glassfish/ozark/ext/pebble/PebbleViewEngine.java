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
package org.glassfish.ozark.ext.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import org.glassfish.ozark.engine.ViewEngineBase;

import javax.enterprise.context.ApplicationScoped;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import java.io.IOException;
import javax.inject.Inject;
import org.glassfish.ozark.engine.ViewEngineConfig;

/**
 * @see <a href="http://www.mitchellbosecke.com/pebble/home">Pebble</a>
 */
@ApplicationScoped
public class PebbleViewEngine extends ViewEngineBase {

  @Inject
  @ViewEngineConfig
  PebbleEngine pebbleEngine;

  @Override
  public boolean supports(String view) {
    return view.endsWith(".peb");
  }

  @Override
  public void processView(ViewEngineContext context) throws ViewEngineException {
    String viewPath = resolveView(context);

    try {
      pebbleEngine.getTemplate(viewPath).evaluate(context.getResponse().getWriter(), context.getModels());
    } catch (PebbleException | IOException ex) {
      throw new ViewEngineException(String.format("Could not process view %s.", context.getView()), ex);
    }
  }
}
