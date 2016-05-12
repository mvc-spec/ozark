/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2015 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.ozark.binding;

import javax.inject.Inject;
import javax.mvc.binding.BindingError;
import javax.mvc.binding.BindingResult;
import javax.mvc.binding.ValidationError;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.Set;

/**
 * Helper class to implement support for {@code javax.mvc.binding.BindingResult}.
 * Has soft dependency with Weld in order to handle proxies; will throw exception if
 * Weld is not found.
 *
 * @author Santiago Pericas-Geertsen
 */
public final class BindingResultUtils {

    private static Class<?> TARGET_INSTANCE;

    static {
        final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            TARGET_INSTANCE = ccl.loadClass("org.jboss.weld.interceptor.util.proxy.TargetInstanceProxy");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private BindingResultUtils() {
        throw new AssertionError("Instantiation not allowed.");
    }

    /**
     * Search for a {@code javax.mvc.binding.BindingResult} field in the resource's
     * class hierarchy. Field must be annotated with {@link javax.inject.Inject}.
     *
     * @param resource resource instance.
     * @return field or {@code null} if none is found.
     */
    private static Field getBindingResultField(final Object resource) {
        Class<?> clazz = resource.getClass();
        do {
            for (Field f : clazz.getDeclaredFields()) {
                // Of BindingResult and CDI injectable
                if (BindingResult.class.isAssignableFrom(f.getType()) && f.getAnnotation(Inject.class) != null) {
                    return f;
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        return null;
    }

    /**
     * Updates the binding error set of {@code javax.mvc.binding.BindingResult}. First
     * checks for an argument, then a property and finally a field.
     *
     * @param resource the resource instance.
     * @param errors   set of errors.
     * @param arg argument in invocation or {@code null}.
     * @return {@code true} if arg, property or field updated, or {@code false} otherwise.
     */
    public static boolean updateBindingResultErrors(Object resource, Set<BindingError> errors,
                                                    BindingResultImpl arg) {
        // Is it in an argument position
        if (arg != null) {
            arg.setErrors(errors);
            return true;
        }

        // Otherwise, check property and then field
        try {
            if (hasBindingResultProperty(resource)) {
                final Object obj = getBindingResultGetter(resource).invoke(resource);
                getSetterMethod(obj, "setErrors").invoke(obj, errors);
            } else {
                // Then check for a field
                final Field vr = getBindingResultField(resource);
                if (vr != null) {
                    AccessController.doPrivileged((java.security.PrivilegedAction<Void>) () -> {
                        vr.setAccessible(true);
                        return null;
                    });
                    final BindingResultImpl value = (BindingResultImpl) vr.get(resource);
                    value.setErrors(errors);
                } else {
                    return false;
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Updates the validation error set of a {@code javax.mvc.binding.BindingResult}.
     * First checks for an argument, then a property and finally a field.
     *
     * @param resource the resource instance.
     * @param validationErrors set of validation errors.
     * @param arg argument in invocation or {@code null}.
     * @return {@code true} if arg, property or field updated, or {@code false} otherwise.
     */
    public static boolean updateBindingResultViolations(Object resource, Set<ValidationError> validationErrors,
                                                        BindingResultImpl arg) {

        // Is it in an argument position
        if (arg != null) {
            arg.setValidationErrors(validationErrors);
            return true;
        }

        // Otherwise, check property and then field
        try {
            if (hasBindingResultProperty(resource)) {
                final Object obj = getBindingResultGetter(resource).invoke(resource);
                getSetterMethod(obj, "setValidationErrors").invoke(obj, validationErrors);
            } else {
                // Then check for a field
                final Field vr = getBindingResultField(resource);
                if (vr != null) {
                    AccessController.doPrivileged((java.security.PrivilegedAction<Void>) () -> {
                        vr.setAccessible(true);
                        return null;
                    });
                    final BindingResultImpl value = (BindingResultImpl) vr.get(resource);
                    value.setValidationErrors(validationErrors);
                } else {
                    return false;
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Searches the class hierarchy for a setter method and returns {@code null} if
     * not found.
     *
     * @param obj  object to use in search.
     * @param name name of method.
     * @return method instance or <code>null</code>.
     */
    private static Method getSetterMethod(Object obj, String name) {
        Class<?> clazz = obj.getClass();
        do {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == 1
                        && m.getParameterTypes()[0] == Set.class) {
                    return m;
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        return null;
    }

    /**
     * Determines if a resource has a property of type {@code javax.mvc.binding.BindingResult}.
     *
     * @param resource resource instance.
     * @return outcome of test.
     */
    private static boolean hasBindingResultProperty(final Object resource) {
        return getBindingResultGetter(resource) != null && getBindingResultSetter(resource) != null;
    }

    /**
     * Returns a getter for {@code javax.mvc.binding.BindingResult} or {@code null}
     * if one cannot be found.
     *
     * @param resource resource instance.
     * @return getter or {@code null} if not available.
     */
    private static Method getBindingResultGetter(final Object resource) {
        Class<?> clazz = resource.getClass();
        do {
            for (Method m : clazz.getDeclaredMethods()) {
                if (isBindingResultGetter(m)) {
                    return m;
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        return null;
    }

    /**
     * Determines if a method is a getter for {@code javax.mvc.binding.BindingResult}.
     *
     * @param m method to test.
     * @return outcome of test.
     */
    private static boolean isBindingResultGetter(Method m) {
        return m.getName().startsWith("get")
                && BindingResult.class.isAssignableFrom(m.getReturnType())
                && Modifier.isPublic(m.getModifiers()) && m.getParameterTypes().length == 0;
    }

    /**
     * Returns a setter for {@code javax.mvc.binding.BindingResult} or {@code null}
     * if one cannot be found.
     *
     * @param resource resource instance.
     * @return setter or {@code null} if not available.
     */
    private static Method getBindingResultSetter(final Object resource) {
        return getBindingResultSetter(resource.getClass());
    }

    /**
     * Returns a setter for a {@code javax.mvc.binding.BindingResult} or {@code null}
     * if none found.
     *
     * @param resourceClass resource class.
     * @return setter or {@code null} if not found.
     */
    private static Method getBindingResultSetter(final Class<?> resourceClass) {
        Class<?> clazz = resourceClass;
        do {
            for (Method m : clazz.getDeclaredMethods()) {
                if (isBindingResultSetter(m)) {
                    return m;
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        return null;
    }

    /**
     * Determines if a method is a setter for {@code javax.mvc.binding.BindingResult}.
     *
     * @param m method to test.
     * @return outcome of test.
     */
    private static boolean isBindingResultSetter(Method m) {
        return m.getName().startsWith("set") && m.getParameterTypes().length == 1
                && BindingResult.class.isAssignableFrom(m.getParameterTypes()[0])
                && m.getReturnType() == Void.TYPE && Modifier.isPublic(m.getModifiers());
    }

    /**
     * Determines if the class of an object is <code>TargetInstanceProxy</code> without introducing
     * a static dependency with Weld.
     *
     * @param obj object to check.
     * @return outcome of test.
     */
    public static boolean isTargetInstanceProxy(Object obj) {
        return TARGET_INSTANCE.isAssignableFrom(obj.getClass());
    }

    /**
     * Invokes <code>getTargetInstance</code> method on obj. Returns <code>null</code>
     * if any exception related to the invocation is thrown.
     *
     * @param obj object on which to call method.
     * @return result of calling method or <code>null</code>.
     */
    public static Object getTargetInstance(Object obj) {
        try {
            final Method m = obj.getClass().getMethod("getTargetInstance");
            return m.invoke(obj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Returns an arbitrary but valid instance of a boxed type or {@code null}. If
     * the type supplied is a primitive type, it is mapped to a boxed type; otherwise
     * {@code null} is returned.
     *
     * @param type type to inspect.
     * @return valid instance or {@code null}.
     */
    public static Object getValidInstanceForType(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return Boolean.TRUE;
            } else if (type == char.class) {
                return Character.valueOf(' ');
            } else if (type == byte.class || type == short.class || type == int.class || type == long.class) {
                return Byte.valueOf((byte) 0);
            } else if (type == double.class || type == float.class) {
                return new Float(0.0);
            }
        }
        return null;
    }
}
