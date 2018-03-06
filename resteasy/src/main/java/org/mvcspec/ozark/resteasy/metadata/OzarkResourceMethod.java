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

import org.jboss.resteasy.spi.metadata.MethodParameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.mvcspec.ozark.util.AnnotationUtils;

import javax.mvc.annotation.Controller;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Wrapper for {@link ResourceMethod} which adds <code>@Produces("text/html")</code> to controller
 * methods if no other media type was specified.
 *
 * @author Christian Kaltepoth
 */
public class OzarkResourceMethod implements ResourceMethod {

    private final ResourceMethod delegate;
    private final MediaType[] produces;

    public OzarkResourceMethod(ResourceMethod delegate) {
        this.delegate = delegate;

        boolean isMediaTypeSpecified = delegate.getProduces() != null && delegate.getProduces().length > 0;

        boolean isControllerMethod = AnnotationUtils.hasAnnotation(delegate.getMethod(), Controller.class)
                || AnnotationUtils.hasAnnotation(delegate.getResourceClass().getClazz(), Controller.class);

        this.produces = isControllerMethod && !isMediaTypeSpecified
                ? new MediaType[]{MediaType.TEXT_HTML_TYPE}
                : delegate.getProduces();

    }

    @Override
    public Set<String> getHttpMethods() {
        return delegate.getHttpMethods();
    }

    @Override
    public MediaType[] getProduces() {
        return produces;
    }

    @Override
    public MediaType[] getConsumes() {
        return delegate.getConsumes();
    }

    @Override
    public boolean isAsynchronous() {
        return delegate.isAsynchronous();
    }

    @Override
    public void markAsynchronous() {
        delegate.markAsynchronous();
    }

    @Override
    public ResourceClass getResourceClass() {
        return delegate.getResourceClass();
    }

    @Override
    public Class<?> getReturnType() {
        return delegate.getReturnType();
    }

    @Override
    public Type getGenericReturnType() {
        return delegate.getGenericReturnType();
    }

    @Override
    public Method getMethod() {
        return delegate.getMethod();
    }

    @Override
    public Method getAnnotatedMethod() {
        return delegate.getAnnotatedMethod();
    }

    @Override
    public MethodParameter[] getParams() {
        return delegate.getParams();
    }

    @Override
    public String getFullpath() {
        return delegate.getFullpath();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }
}
