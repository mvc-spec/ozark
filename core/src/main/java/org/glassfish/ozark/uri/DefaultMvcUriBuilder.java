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
package org.glassfish.ozark.uri;

import javax.mvc.MvcUriBuilder;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Default implementation of {@link MvcUriBuilder}.</p>
 *
 * @author Florian Hirsch
 */
class DefaultMvcUriBuilder implements MvcUriBuilder {

    private final UriTemplate uriTemplate;

    private MultivaluedMap<String, Object> parameters = new MultivaluedHashMap<>();

    DefaultMvcUriBuilder(UriTemplate UriTemplate) {
        Objects.requireNonNull(UriTemplate, "uriTemplate must not be null");
        this.uriTemplate = UriTemplate;
    }

    @Override
    public MvcUriBuilder param(String name, Object... values) {
        Objects.requireNonNull(values, "name must not be null");
        Objects.requireNonNull(values, "values must not be null");
        Arrays.stream(values).forEach(value -> {
            // ensure that an Iterable is not interpreted as one Object
            Iterable<Object> vals = value instanceof Iterable
                ? (Iterable) value : Collections.singleton(value);
            vals.forEach(val -> parameters.add(name, val));
        });
        return this;
    }

    @Override
    public URI build() {
        UriBuilder uriBuilder = UriBuilder.fromUri(uriTemplate.path());
        Map<String, Object> pathParams = new HashMap<>();
        // Everything which is not defined as query- or matrix-param should
        // be a path-param and appear only once
        parameters.forEach((key, value) -> {
            if (uriTemplate.queryParams().contains(key)) {
                value.forEach(val -> uriBuilder.queryParam(key, val));
            } else if (uriTemplate.matrixParams().contains(key)) {
                value.forEach(val -> uriBuilder.matrixParam(key, val));
            } else {
                // won't allow different values for the same path identifier
                pathParams.put(key, value.get(0));
            }
        });
        return uriBuilder.buildFromMap(pathParams);
    }

}
