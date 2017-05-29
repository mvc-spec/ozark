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

import org.glassfish.ozark.engine.ViewEngineConfig;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Producer for the Freemarker {@link freemarker.template.Configuration} used by
 * {@link org.glassfish.ozark.ext.freemarker.FreemarkerViewEngine}.
 *
 * @author Christian Kaltepoth
 */
public class DefaultConfigurationProducer {

    @Inject
    private ServletContext servletContext;

    @Produces
    @ViewEngineConfig
    public Configuration getConfiguration() {

        Configuration configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateLoader(new TemplateLoader() {

            @Override
            public Object findTemplateSource(String s) throws IOException {
                return servletContext.getResourceAsStream("/" + s);     // Freemarker drops "/"
            }

            @Override
            public long getLastModified(Object o) {
                return -1;
            }

            @Override
            public Reader getReader(Object o, String s) throws IOException {
                return new InputStreamReader((InputStream) o);
            }

            @Override
            public void closeTemplateSource(Object o) throws IOException {
                ((InputStream) o).close();
            }
        });

        return configuration;

    }

}
