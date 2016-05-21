/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.ozark.cdi;

import org.glassfish.ozark.MvcContextImpl;
import org.glassfish.ozark.binding.BindingInterceptorImpl;
import org.glassfish.ozark.binding.BindingResultImpl;
import org.glassfish.ozark.binding.ConstraintViolationTranslator;
import org.glassfish.ozark.core.*;
import org.glassfish.ozark.engine.FaceletsViewEngine;
import org.glassfish.ozark.engine.JspViewEngine;
import org.glassfish.ozark.engine.ViewEngineFinder;
import org.glassfish.ozark.event.*;
import org.glassfish.ozark.locale.DefaultLocaleResolver;
import org.glassfish.ozark.locale.LocaleRequestFilter;
import org.glassfish.ozark.locale.LocaleResolverChain;
import org.glassfish.ozark.security.CsrfImpl;
import org.glassfish.ozark.security.CsrfProtectFilter;
import org.glassfish.ozark.security.CsrfValidateInterceptor;
import org.glassfish.ozark.security.EncodersImpl;
import org.glassfish.ozark.util.CdiUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.mvc.annotation.RedirectScoped;
import javax.mvc.event.MvcEvent;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Class OzarkCdiExtension. Initialize redirect scope as CDI scope. Collect information
 * about all MVC events being observed by the application to optimize event creation
 * and firing.
 *
 * @author Santiago Pericas-Geertsen
 * @author Manfred Riem
 */
@SuppressWarnings("unchecked")
public class OzarkCdiExtension implements Extension {

    private static Set<Class<? extends MvcEvent>> observedEvents;

    /**
     * Before bean discovery.
     *
     * @param event the event.
     * @param beanManager the bean manager.
     */
    public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery event, BeanManager beanManager) {
        event.addScope(RedirectScoped.class, true, true);

        CdiUtils.addAnnotatedTypes(event, beanManager,

                // .
                MvcContextImpl.class,

                // binding
                BindingResultImpl.class,
                BindingInterceptorImpl.class,
                ConstraintViolationTranslator.class,

                // core
                Messages.class,
                ModelsImpl.class,
                ViewableWriter.class,
                ViewRequestFilter.class,
                ViewResponseFilter.class,

                // engine
                FaceletsViewEngine.class,
                JspViewEngine.class,
                ViewEngineFinder.class,

                // security
                CsrfImpl.class,
                CsrfProtectFilter.class,
                CsrfValidateInterceptor.class,
                EncodersImpl.class,

                // util
                CdiUtils.class,

                // cdi
                RedirectScopeManager.class,

                //event
                AfterControllerEventImpl.class,
                AfterProcessViewEventImpl.class,
                BeforeControllerEventImpl.class,
                BeforeProcessViewEventImpl.class,
                ControllerRedirectEventImpl.class,
                MvcEventImpl.class,

                //locale
                LocaleRequestFilter.class,
                LocaleResolverChain.class,
                DefaultLocaleResolver.class

        );
    }

    /**
     * After bean discovery.
     *
     * @param event the event.
     * @param beanManager the bean manager.
     */
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery event, BeanManager beanManager) {
        event.addContext(new RedirectScopeContext());
    }

    /**
     * Gather set of event types that are observed by MVC application. This info is later
     * used to optimize event creation and firing.
     *
     * @param pom process observer method object.
     * @param beanManager the bean manager.
     * @param <T> the type of the event being observed.
     * @param <X> the bean type containing the observer method.
     */
    public <T, X> void processObserverMethod(@Observes ProcessObserverMethod<T, X> pom, BeanManager beanManager) {
        final Type type = pom.getObserverMethod().getObservedType();
        if (type instanceof Class<?>) {
            final Class<?> clazz = (Class<?>) type;
            if (MvcEvent.class.isAssignableFrom(clazz)) {
                addObservedEvent((Class<? extends MvcEvent>) type);
            }
        }
    }

    /**
     * Add MVC event type to set of observed events.
     *
     * @param eventType event type.
     */
    public static synchronized void addObservedEvent(Class<? extends MvcEvent> eventType) {
        if (observedEvents == null) {
            observedEvents = new HashSet<>();
        }
        observedEvents.add(eventType);
    }

    /**
     * Determine if an event type is being observed.
     *
     * @param eventType event type.
     * @return outcome of test.
     */
    public static synchronized boolean isEventObserved(Class<? extends MvcEvent> eventType) {
        return observedEvents == null ? false : observedEvents.contains(eventType);
    }
}
