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
package org.mvcspec.ozark.binding;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for working with {@link ConstraintViolation}.
 *
 * @author Christian Kaltepoth
 */
public class ConstraintViolationUtils {

    /**
     * Returns the parameter name to which a {@link ConstraintViolation} refers.
     */
    public static String getParamName(ConstraintViolation<?> violation) {

        // create a simple list of nodes from the path
        List<Path.Node> nodes = new ArrayList<>();
        for (Path.Node node : violation.getPropertyPath()) {
            nodes.add(node);
        }
        Path.Node lastNode = nodes.get(nodes.size() - 1);

        // the path refers to some property of the leaf bean
        if (lastNode.getKind() == ElementKind.PROPERTY) {

            Path.PropertyNode propertyNode = lastNode.as(Path.PropertyNode.class);
            Annotation[] annotations = getPropertyAnnotations(violation, propertyNode);

            return getBeanNameFromAnnotation(annotations);

        }

        // The path refers to a method parameter
        else if (lastNode.getKind() == ElementKind.PARAMETER && nodes.size() == 2) {

            Path.MethodNode methodNode = nodes.get(0).as(Path.MethodNode.class);
            Path.ParameterNode parameterNode = nodes.get(1).as(Path.ParameterNode.class);

            Annotation[] annotations = getParameterAnnotations(violation, methodNode, parameterNode);

            return getBeanNameFromAnnotation(annotations);

        }

        return null;

    }

    private static Annotation[] getPropertyAnnotations(ConstraintViolation<?> violation, Path.PropertyNode node) {

        try {

            Class<?> leafBeanClass = violation.getLeafBean().getClass();
            Field field = leafBeanClass.getDeclaredField(node.getName());

            return field.getAnnotations();

        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }

    }

    private static Annotation[] getParameterAnnotations(ConstraintViolation<?> violation, Path.MethodNode methodNode,
                                                        Path.ParameterNode parameterNode) {

        try {

            String methodName = methodNode.getName();

            int paramCount = methodNode.getParameterTypes().size();
            Class[] paramTypes = methodNode.getParameterTypes().toArray(new Class[paramCount]);

            Class<?> rootBeanClass = violation.getRootBean().getClass();
            Method method = rootBeanClass.getMethod(methodName, paramTypes);

            int parameterIndex = parameterNode.getParameterIndex();
            return method.getParameterAnnotations()[parameterIndex];

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }

    }

    private static String getBeanNameFromAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam) {
                return ((QueryParam) annotation).value();
            }
            if (annotation instanceof PathParam) {
                return ((PathParam) annotation).value();
            }
            if (annotation instanceof FormParam) {
                return ((FormParam) annotation).value();
            }
            if (annotation instanceof MatrixParam) {
                return ((MatrixParam) annotation).value();
            }
            if (annotation instanceof CookieParam) {
                return ((CookieParam) annotation).value();
            }
        }
        return null;
    }

}
