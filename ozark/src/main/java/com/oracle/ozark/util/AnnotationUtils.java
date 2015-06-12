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
package com.oracle.ozark.util;

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
     * the class and if that fails, try the super class. Note that there is no special
     * processing required for method annotations in proxied classes. Ideally this
     * method should be part of the CDI API.
     *
     * @param clazz class to search annotation.
     * @param annotationType type of annotation to search for.
     * @return annotation instance or {@code null} if none found.
     */
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        final T an = clazz.getDeclaredAnnotation(annotationType);
        if (an != null) {
            return an;
        }
        // Weld proxies require inspecting the superclass
        return isWeldProxy(clazz) ? clazz.getSuperclass().getDeclaredAnnotation(annotationType) : null;
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
        if (hasMvcAnnotations(method)) {
            return null;
        } else {
            // Search for overridden method in super class
            final Class<?> superClass = method.getDeclaringClass().getSuperclass();
            try {
                final Method superMethod = superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                an = getAnnotation(superMethod, annotationType);
            } catch (NoSuchMethodException e) {
                // falls through
            }
            if (an != null) {
                return an;
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
     * Determines if a method has one or more MVC annotations on it.
     *
     * @param method method to check for MVC annotations.
     * @return outcome of test.
     */
    private static boolean hasMvcAnnotations(Method method) {
        final List<Annotation> ans = Arrays.asList(method.getDeclaredAnnotations());
        return ans.stream().anyMatch(a -> a.getClass().getName().startsWith("javax.mvc."));
    }

    /**
     * All Weld proxies should implement this interface.
     */
    private static final String PROXY_OBJECT = "org.jboss.weld.bean.proxy.ProxyObject";

    /**
     * Determines if a class represents a Weld proxy. This code is naturally not portable
     * across CDI implementations.
     *
     * @param clazz class to check if it is a Weld proxy.
     * @return outcome of test.
     */
    private static boolean isWeldProxy(Class<?> clazz) {
        return Arrays.asList(clazz.getInterfaces()).stream().anyMatch(in -> in.getName().equals(PROXY_OBJECT));
    }
}
