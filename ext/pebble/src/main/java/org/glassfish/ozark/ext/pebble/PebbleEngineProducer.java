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

import com.google.common.cache.CacheBuilder;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.glassfish.ozark.engine.ViewEngineConfig;

public class PebbleEngineProducer {

  @Inject
  private Properties pebbleConfiguration;

  @Inject
  private ServletContext servletContext;

  @Produces
  @ViewEngineConfig
  public PebbleEngine pebbleEngine() {
    PebbleEngine.Builder engine = new PebbleEngine.Builder();

    pebbleConfiguration.forEach((key, value) -> {
      switch (PebbleProperty.fromKey(String.valueOf(key))) {
        case AUTO_ESCAPING:
          engine.autoEscaping(Boolean.valueOf(String.valueOf(value)));
          break;
        case CACHE_ACTIVE:
          engine.cacheActive(Boolean.valueOf(String.valueOf(value)));
          break;
        case ESCAPING_STRATEGY:
          try {
            engine.addEscapingStrategy("userDefinedEscapingStrategy", (EscapingStrategy) Class.forName(String.valueOf(value)).newInstance());
            engine.defaultEscapingStrategy("userDefinedEscapingStrategy");
          } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            String msg = String.format("Pebble initialization error: Could not register escaping strategy '%s' of type %s", key, value);
            throw new IllegalArgumentException(msg, ex);
          }
          break;
        case DEFAULT_LOCALE:
          engine.defaultLocale(Locale.forLanguageTag(String.valueOf(value)));
          break;
        case NEW_LINE_TRIMMING:
          engine.newLineTrimming(Boolean.valueOf(String.valueOf(value)));
          break;
        case STRICT_VARIABLES:
          engine.strictVariables(Boolean.valueOf(String.valueOf(value)));
          break;
        case EXECUTOR_SERVICE:
          try {
            engine.executorService((ExecutorService) Class.forName(String.valueOf(value)).newInstance());
          } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            String msg = String.format("Pebble initialization error: Could not register executor service type %s", value);
            throw new IllegalArgumentException(msg, ex);
          }
          break;
        case EXTENSION:
          String[] extensions = String.valueOf(value).split(",");

          Extension[] extensionArray = (Extension[]) Stream.of(extensions)
              .map(clazzName -> {
                try {
                  return (Extension) Class.forName(clazzName).newInstance();
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                  String msg = String.format("Pebble initialization error: Could not register extension of type %s", clazzName);
                  throw new IllegalArgumentException(msg, ex);
                }
              })
              .collect(Collectors.toList())
              .toArray();

          engine.extension(extensionArray);
          break;
        case TAG_CACHE_MAX:
          engine.tagCache(CacheBuilder.newBuilder().maximumSize(Integer.valueOf(String.valueOf(value))).build());
          break;
        case TEMPLATE_CACHE_MAX:
          engine.templateCache(CacheBuilder.newBuilder().maximumSize(Integer.valueOf(String.valueOf(value))).build());
          break;
        case UNKNOWN:
          break;
        default:
          break;
      }
    });

    engine.loader(new ServletLoader(servletContext));
    
    return engine.build();
  }

}
