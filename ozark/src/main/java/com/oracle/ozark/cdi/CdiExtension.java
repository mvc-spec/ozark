package com.oracle.ozark.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducer;

/**
 * CdiExtension class
 *
 * @author Santiago.PericasGeertsen@oracle.com
 */
public class CdiExtension implements Extension {

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat,
            BeanManager beanManager) {
        System.out.println("scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
    }

    void decorateEntityManager(@Observes ProcessProducer<?, ?> pp) {
        System.out.println("Process producer " + pp.getProducer());
    }
}
