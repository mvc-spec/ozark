/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.ozark.util;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods to lookup annotations.
 *
 * @author Santiago Pericas-Geertsen
 */
public final class AnnotationUtils {

    /**
     * Retrieve an annotation from a possibly proxied CDI/Weld class. First inspect
     * the class and if that fails, try using an annotated type obtained from CDI's
     * bean manager.
     *
     * @param clazz          class to search annotation.
     * @param annotationType type of annotation to search for.
     * @param <T> annotation subclass.
     * @return annotation instance or {@code null} if none found.
     */
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        final T an = clazz.getDeclaredAnnotation(annotationType);
        if (an != null) {
            return an;
        }
        final BeanManager bm = CDI.current().getBeanManager();
        final AnnotatedType<?> type = bm.createAnnotatedType(clazz);
        return type != null ? type.getAnnotation(annotationType) : null;
    }

    /**
     * Determines if an annotation is present on a class by calling {@link #getAnnotation(Class, Class)}.
     *
     * @param clazz class to search annotation.
     * @param annotationType type of annotation to search for.
     * @param <T> annotation subclass.
     * @return outcome of test.
     */
    public static <T extends Annotation> boolean hasAnnotation(Class<?> clazz, Class<T> annotationType) {
        return getAnnotation(clazz, annotationType) != null;
    }

    /**
     * Search for a method annotation following the inheritance rules defined by the
     * JAX-RS specification, and also stated in the MVC specification. If an annotation is
     * not defined on a method, check super methods along the class hierarchy first. If
     * not found, then look at the interface hierarchy. Note that this method implements
     * a depth-first search strategy.
     *
     * @param method method to start search at.
     * @param annotationType annotation class to search for.
     * @param <T> annotation subclass.
     * @return annotation instances or {@code null} if not found.
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationType) {
        // If we reached Object.class, we couldn't find it
        final Class<?> clazz = method.getDeclaringClass();
        if (clazz == Object.class) {
            return null;
        }

        // Check if annotation declared (but not inherited) on method
        T an = method.getDeclaredAnnotation(annotationType);
        if (an != null) {
            return an;
        }

        // Other MVC annotations on this method, then inheritance disabled
        if (hasMvcOrJaxrsAnnotations(method)) {
            return null;
        } else {
            // Search for overridden method in super class
            final Class<?> superClass = method.getDeclaringClass().getSuperclass();
            if (superClass != null) {
                try {
                    final Method superMethod = superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    an = getAnnotation(superMethod, annotationType);
                } catch (NoSuchMethodException e) {
                    // falls through
                }
                if (an != null) {
                    return an;
                }
            }

            // Now search for overridden method in super interfaces
            final Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
            for (Class<?> in : interfaces) {
                try {
                    final Method superMethod = in.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    an = getAnnotation(superMethod, annotationType);
                } catch (NoSuchMethodException e) {
                    // falls through
                }
                if (an != null) {
                    return an;
                }
            }

            // Not found, return null
            return null;
        }
    }

    /**
     * Determines if an annotation is present on a method by calling
     * {@link #getAnnotation(java.lang.reflect.Method, Class)}.
     *
     * @param <T> the type.
     * @param method method to start search at..
     * @param annotationType type of annotation to search for.
     * @return outcome of test.
     */
    public static <T extends Annotation> boolean hasAnnotation(Method method, Class<T> annotationType) {
        return getAnnotation(method, annotationType) != null;
    }

    /**
     * Determines if a method has one or more MVC or JAX-RS annotations on it.
     *
     * @param method method to check for MVC or JAX-RS annotations.
     * @return outcome of test.
     */
    private static boolean hasMvcOrJaxrsAnnotations(Method method) {
        final List<Annotation> ans = Arrays.asList(method.getDeclaredAnnotations());
        return ans.stream().anyMatch(a -> {
            final String an = a.annotationType().getName();
            return an.startsWith("javax.mvc.") || an.startsWith("javax.ws.rs.");
        });
    }
}
