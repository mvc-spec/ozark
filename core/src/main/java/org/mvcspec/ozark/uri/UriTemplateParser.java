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
package org.mvcspec.ozark.uri;

import org.mvcspec.ozark.util.AnnotationUtils;
import org.mvcspec.ozark.util.BeanUtils;
import org.mvcspec.ozark.util.ControllerUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.mvc.MvcContext;
import javax.ws.rs.BeanParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Parses all instances of {@link UriTemplate} and @Produces
 * the {@link ApplicationUris}.</p>
 *
 * @author Florian Hirsch
 */
@ApplicationScoped
public class UriTemplateParser {

    @Inject
    MvcContext mvcContext;

    @Context
    Application application;

    @Produces @ApplicationScoped
    private ApplicationUris applicationUris;

    @PostConstruct
    public void init() {
        applicationUris = init(controllers());
    }

    ApplicationUris init(Set<Class<?>> controllers) {
        ApplicationUris uris = new ApplicationUris();
        controllers.forEach(controller ->
            Stream.of(controller.getMethods()).filter(ControllerUtils::isControllerMethod).forEach(method -> {
                UriTemplate uriTemplate = parseMethod(method, mvcContext.getBasePath());
                uris.register(uriTemplate, method);
            })
        );
        return uris;
    }

    /**
     * <p>Parses given method and constructs a {@link UriTemplate} containing
     * all the information found in the annotations {@link javax.ws.rs.Path},
     * {@link javax.ws.rs.QueryParam} and {@link javax.ws.rs.MatrixParam}.</p>
     */
    UriTemplate parseMethod(Method method, String basePath) {
        UriBuilder uriBuilder = UriBuilder.fromPath(basePath);
        Path controllerPath = AnnotationUtils.getAnnotation(method.getDeclaringClass(), Path.class);
        if (controllerPath != null) {
            uriBuilder.path(controllerPath.value());
        }
        Path methodPath = AnnotationUtils.getAnnotation(method, Path.class);
        if (methodPath != null) {
            uriBuilder.path(methodPath.value());
        }
        UriTemplate.Builder uriTemplateBuilder = UriTemplate.fromTemplate(uriBuilder.toTemplate());
        // Populate a List with all properties of given target and all parameters of given method
        // except for BeanParams where we need all properties of annotated type.
        List<AnnotatedElement> annotatedElements = BeanUtils.getFieldsAndAccessors(method.getDeclaringClass());
        Arrays.asList(method.getParameters()).forEach(param -> {
            if (param.isAnnotationPresent(BeanParam.class)) {
                annotatedElements.addAll(BeanUtils.getFieldsAndAccessors(param.getType()));
            } else {
                annotatedElements.add(param);
            }
        });
        annotatedElements.forEach(accessibleObject -> {
            if (accessibleObject.isAnnotationPresent(QueryParam.class)) {
                uriTemplateBuilder.queryParam(accessibleObject.getAnnotation(QueryParam.class).value());
            }
            if (accessibleObject.isAnnotationPresent(MatrixParam.class)) {
                uriTemplateBuilder.matrixParam(accessibleObject.getAnnotation(MatrixParam.class).value());
            }
        });
        return uriTemplateBuilder.build();
    }

    private Set<Class<?>> controllers() {
        return Stream.concat( //
            application.getClasses().stream(), //
            application.getSingletons().stream().map(Object::getClass)) //
            .filter(ControllerUtils::isController) //
            .collect(Collectors.toSet()); //
    }

}
