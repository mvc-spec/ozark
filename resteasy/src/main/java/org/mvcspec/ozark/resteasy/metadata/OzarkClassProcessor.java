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
package org.mvcspec.ozark.resteasy.metadata;

import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import org.mvcspec.ozark.util.AnnotationUtils;

import javax.mvc.annotation.Controller;
import javax.ws.rs.ext.Provider;

/**
 * Custom implementation of the RESTEasy {@link ResourceClassProcessor} SPI which adds
 * <code>@Produces("text/html")</code> to controller methods if they don't define some other
 * media type.
 *
 * @author Christian Kaltepoth
 */
@Provider
public class OzarkClassProcessor implements ResourceClassProcessor {

    @Override
    public ResourceClass process(ResourceClass clazz) {

        // we only need to wrap resources with at least one controller method here
        if (AnnotationUtils.hasAnnotationOnClassOrMethod(clazz.getClazz(), Controller.class)) {
            return new OzarkResourceClass(clazz);
        }

        return clazz;

    }

}
