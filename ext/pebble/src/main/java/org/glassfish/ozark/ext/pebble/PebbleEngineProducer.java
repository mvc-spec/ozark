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
import com.mitchellbosecke.pebble.loader.ServletLoader;
import java.util.Properties;
import org.glassfish.ozark.engine.ViewEngineConfig;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

public class PebbleEngineProducer {

  @Inject
  private Properties pebbleProperties;

  @Inject
  private ServletContext servletContext;

  @Produces
  @ViewEngineConfig
  public PebbleEngine pebbleEngine() {
    return new PebbleEngine.Builder()
        .loader(new ServletLoader(servletContext))
        .build();
  }
}
