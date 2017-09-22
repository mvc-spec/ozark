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
package org.mvcspec.ozark.cdi;

import org.mvcspec.ozark.MvcContextImpl;
import org.mvcspec.ozark.OzarkConfig;
import org.mvcspec.ozark.binding.BeanValidationProducer;
import org.mvcspec.ozark.binding.BindingResultManager;
import org.mvcspec.ozark.binding.ConstraintViolationTranslator;
import org.mvcspec.ozark.binding.convert.ConverterRegistry;
import org.mvcspec.ozark.binding.convert.MvcConverterProvider;
import org.mvcspec.ozark.cdi.types.AnnotatedTypeProcessor;
import org.mvcspec.ozark.core.*;
import org.mvcspec.ozark.engine.FaceletsViewEngine;
import org.mvcspec.ozark.engine.JspViewEngine;
import org.mvcspec.ozark.engine.ViewEngineFinder;
import org.mvcspec.ozark.event.*;
import org.mvcspec.ozark.jaxrs.JaxRsContextFilter;
import org.mvcspec.ozark.jaxrs.JaxRsContextProducer;
import org.mvcspec.ozark.locale.DefaultLocaleResolver;
import org.mvcspec.ozark.locale.LocaleRequestFilter;
import org.mvcspec.ozark.locale.LocaleResolverChain;
import org.mvcspec.ozark.security.CsrfImpl;
import org.mvcspec.ozark.security.CsrfProtectFilter;
import org.mvcspec.ozark.security.CsrfTokenManager;
import org.mvcspec.ozark.security.CsrfValidateInterceptor;
import org.mvcspec.ozark.security.EncodersImpl;
import org.mvcspec.ozark.uri.ApplicationUris;
import org.mvcspec.ozark.uri.UriTemplateParser;
import org.mvcspec.ozark.util.CdiUtils;
import org.mvcspec.ozark.validation.ValidationInterceptor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.mvc.annotation.Controller;
import javax.mvc.annotation.RedirectScoped;
import javax.mvc.event.MvcEvent;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class OzarkCdiExtension. Initialize redirect scope as CDI scope. Collect information
 * about all MVC events being observed by the application to optimize event creation
 * and firing.
 *
 * @author Santiago Pericas-Geertsen
 * @author Manfred Riem
 * @author Christian Kaltepoth
 */
@SuppressWarnings("unchecked")
public class OzarkCdiExtension implements Extension {

    private static final Logger log = Logger.getLogger(OzarkCdiExtension.class.getName());

    private static Set<Class<? extends MvcEvent>> observedEvents;

    private final AnnotatedTypeProcessor annotatedTypeProcessor = new AnnotatedTypeProcessor();

    /**
     * Before bean discovery.
     *
     * @param event the event.
     * @param beanManager the bean manager.
     */
    public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery event, BeanManager beanManager) {

        log.fine("Observed BeforeBeanDiscovery event, registering scopes and beans...");

        event.addScope(RedirectScoped.class, true, true);

        CdiUtils.addAnnotatedTypes(event, beanManager,

                // .
                MvcContextImpl.class,
                OzarkConfig.class,

                // binding
                BeanValidationProducer.class,
                BindingResultManager.class,
                ConstraintViolationTranslator.class,
                ConverterRegistry.class,
                MvcConverterProvider.class,

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
                CsrfTokenManager.class,
                EncodersImpl.class,

                // util
                CdiUtils.class,

                // cdi
                RedirectScopeManager.class,
                ValidationInterceptor.class,

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
                DefaultLocaleResolver.class,

                // jaxrs
                JaxRsContextFilter.class,
                JaxRsContextProducer.class,

                // uri
                ApplicationUris.class,
                UriTemplateParser.class

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
     * Search for {@link javax.mvc.annotation.Controller} annotation and patch AnnotatedType
     */
    public <T> void processAnnotatedType(@Observes @WithAnnotations({Controller.class}) ProcessAnnotatedType<T> pat) {

        AnnotatedType<T> replacement = annotatedTypeProcessor.getReplacement(pat.getAnnotatedType());
        if (replacement != null) {
            log.log(Level.FINE, "Replacing AnnotatedType of class: {0}", replacement.getJavaClass().getName());
            pat.setAnnotatedType(replacement);
        }

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
