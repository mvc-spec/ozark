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
package com.oracle.ozark.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Utility class for CDI-related tasks. This is a CDI class itself and can be
 * injected to call its methods.
 *
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
public class CdiUtil {

    @Inject
    private BeanManager bm;

    /**
     * Create a new CDI bean given its class. The bean is created in the context
     * defined by the scope annotation on the class.
     *
     * @param clazz CDI class.
     * @param <T>   Class parameter.
     * @return Newly allocated CDI bean.
     */
    @SuppressWarnings("unchecked")
    public <T> T newBean(Class<T> clazz) {
        Set<Bean<?>> beans = bm.getBeans(clazz);
        final Bean<T> bean = (Bean<T>) bm.resolve(beans);
        final CreationalContext<T> ctx = bm.createCreationalContext(bean);
        return (T) bm.getReference(bean, clazz, ctx);
    }

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
        final T an = clazz.getAnnotation(annotationType);
        if (an != null) {
            return an;
        }
        // CDI/Weld proxies may require to inspect super class
        return clazz.getSuperclass().getAnnotation(annotationType);
    }
}
