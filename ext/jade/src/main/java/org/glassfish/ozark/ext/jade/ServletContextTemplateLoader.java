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
package org.glassfish.ozark.ext.jade;

import de.neuland.jade4j.template.TemplateLoader;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * A TemplateLoader using {@link ServletContext#getResource(String)} for loading
 * the given viewPath.
 *
 * @author Florian Hirsch
 */
class ServletContextTemplateLoader implements TemplateLoader {

    private final ServletContext servletContext;

    private final String encoding;

    public ServletContextTemplateLoader(ServletContext servletContext, String encoding) {
        Objects.requireNonNull(servletContext, "servletContext must not be null!");
        Objects.requireNonNull(encoding, "encoding must not be null!");
        this.servletContext = servletContext;
        this.encoding = encoding;
    }

    @Override
    public Reader getReader(String name) throws IOException {
        InputStream inputStream = servletContext.getResourceAsStream(name);
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("Jade Template '%s' not found.", name));
        }
        return new BufferedReader(new InputStreamReader(inputStream, Charset.forName(encoding)));
    }

    @Override
    public long getLastModified(String name) throws IOException {
        String path = servletContext.getRealPath(name);
        if (path == null) {
            return -1;
        }
        File template = new File(path);
        if (!template.exists()) {
            return -1;
        }
        return template.lastModified();
    }
}
