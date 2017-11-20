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
package org.mvcspec.ozark.binding.validate;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class to create {@link ConstraintViolationMetadata} from constraint violations.
 *
 * @author Christian Kaltepoth
 */
public class ConstraintViolations {

    private static final Logger log = Logger.getLogger(ConstraintViolations.class.getName());

    private ConstraintViolations() {
        // utility class
    }

    public static ConstraintViolationMetadata getMetadata(ConstraintViolation<?> violation) {

        Annotation[] annotations = getAnnotations(violation);

        return new ConstraintViolationMetadata(violation, annotations);

    }

    private static Annotation[] getAnnotations(ConstraintViolation<?> violation) {


        // create a simple list of nodes from the path
        List<Path.Node> nodes = new ArrayList<>();
        for (Path.Node node : violation.getPropertyPath()) {
            nodes.add(node);
        }
        Path.Node lastNode = nodes.get(nodes.size() - 1);

        // the path refers to some property of the leaf bean
        if (lastNode.getKind() == ElementKind.PROPERTY) {

            Path.PropertyNode propertyNode = lastNode.as(Path.PropertyNode.class);
            return getPropertyAnnotations(violation, propertyNode);

        }

        // The path refers to a method parameter
        else if (lastNode.getKind() == ElementKind.PARAMETER && nodes.size() == 2) {

            Path.MethodNode methodNode = nodes.get(0).as(Path.MethodNode.class);
            Path.ParameterNode parameterNode = nodes.get(1).as(Path.ParameterNode.class);

            return getParameterAnnotations(violation, methodNode, parameterNode);

        }


        log.warning("Could not read annotations for path: " + violation.getPropertyPath().toString());
        return new Annotation[0];

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

}
