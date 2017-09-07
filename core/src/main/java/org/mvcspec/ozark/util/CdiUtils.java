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
package org.mvcspec.ozark.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for CDI-related tasks. This is a CDI class itself and can be
 * injected to call its methods.
 *
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
@SuppressWarnings("unchecked")
public class CdiUtils {

    @Inject
    private BeanManager beanManager;

    /**
     * Create a new CDI bean given its class. The bean is created in the context
     * defined by the scope annotation on the class.
     *
     * @param clazz CDI class.
     * @param <T>   class parameter.
     * @return newly allocated CDI bean.
     */
    @SuppressWarnings("unchecked")
    public <T> T newBean(Class<T> clazz) {
        return newBean(beanManager, clazz);
    }

    /**
     * Create a new CDI bean given its class and a bean manager. The bean is created
     * in the context defined by the scope annotation on the class.
     *
     * @param bm The BeanManager.
     * @param clazz CDI class.
     * @param <T>   class parameter.
     * @return newly allocated CDI bean.
     */
    public static <T> T newBean(BeanManager bm, Class<T> clazz) {
        Set<Bean<?>> beans = bm.getBeans(clazz);
        final Bean<T> bean = (Bean<T>) bm.resolve(beans);
        final CreationalContext<T> ctx = bm.createCreationalContext(bean);
        return (T) bm.getReference(bean, clazz, ctx);
    }

    /**
     * @param beforeBean  The BeforeBeanDiscovery.
     * @param bm The BeanManager.
     * @param types annotated types to register
     */
    public static void addAnnotatedTypes(BeforeBeanDiscovery beforeBean, BeanManager bm, Class<?>... types) {
        for (Class<?> type : types) {
            beforeBean.addAnnotatedType(bm.createAnnotatedType(type), type.getName());
        }
    }

    /**
     * Returns a list of CDI beans with the specified bean type and qualifiers.
     * Please note that this method supports looking up beans deployed with the application
     * even if Ozark is deployed as a container archive.
     */
    public static <T> List<T> getApplicationBeans(Class<T> type, Annotation... qualifiers) {
        BeanManager manager = getApplicationBeanManager();
        return manager.getBeans(type, qualifiers).stream()
                .map(bean -> (T) manager.getReference(bean, type, manager.createCreationalContext(bean)))
                .collect(Collectors.toList());
    }

    /**
     * This method returns a {@link BeanManager} which can resolve beans defined in the application.
     * In case of Glassfish the injected {@link BeanManager} doesn't work here as it only resolves
     * beans in the Ozark archive if Ozark is installed as part of the container.
     */
    public static BeanManager getApplicationBeanManager() {
        return CDI.current().getBeanManager();
    }

}
