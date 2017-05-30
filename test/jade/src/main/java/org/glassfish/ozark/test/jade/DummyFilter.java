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
package org.glassfish.ozark.test.jade;

import de.neuland.jade4j.filter.Filter;

import java.util.Map;

/**
 * Used to test if configuration works.
 *
 * @author Florian Hirsch
 */
public class DummyFilter implements Filter {

    @Override
    public String convert(String source, Map<String, Object> attributes, Map<String, Object> model) {
        String content = source.trim();
        return String.format("<p class='%s'>%s</p>", content, content);
    }
}
