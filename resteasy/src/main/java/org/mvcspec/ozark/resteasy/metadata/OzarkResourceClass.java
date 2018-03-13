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

import org.jboss.resteasy.spi.metadata.FieldParameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.spi.metadata.SetterParameter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Wrapper for {@link ResourceClass} which wraps methods in {@link OzarkResourceMethod}.
 *
 * @author Christian Kaltepoth
 */
public class OzarkResourceClass implements ResourceClass {

    private final ResourceClass delegate;
    private final ResourceMethod[] resourceMethods;

    public OzarkResourceClass(ResourceClass delegate) {
        this.delegate = delegate;

        // wrap methods so we can add @Produces("text/html") if required
        this.resourceMethods = Arrays.stream(this.delegate.getResourceMethods())
                .map(OzarkResourceMethod::new)
                .collect(Collectors.toList())
                .toArray(new ResourceMethod[0]);

    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public Class<?> getClazz() {
        return delegate.getClazz();
    }

    @Override
    public ResourceConstructor getConstructor() {
        return delegate.getConstructor();
    }

    @Override
    public FieldParameter[] getFields() {
        return delegate.getFields();
    }

    @Override
    public SetterParameter[] getSetters() {
        return delegate.getSetters();
    }

    @Override
    public ResourceMethod[] getResourceMethods() {
        return resourceMethods;
    }

    @Override
    public ResourceLocator[] getResourceLocators() {
        return delegate.getResourceLocators();
    }
}
