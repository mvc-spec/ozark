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
package org.glassfish.ozark.locale;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.locale.LocaleResolver;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implements the locale resolving algorithm described in {@link LocaleResolver}.
 *
 * @author Christian Kaltepoth
 */
@ApplicationScoped
public class LocaleResolverChain {

    @Inject
    @Any
    private Instance<LocaleResolver> resolvers;

    @Inject
    private HttpServletRequest request;

    @Context
    private Configuration configuration;

    @PostConstruct
    public void verify() {
        Objects.requireNonNull(configuration, "The Configuration instance was not injected! " +
                "Please make sure you are using a recent version of Jersey.");
    }

    public Locale resolve() {

        // prepare context instance
        LocaleResolverContextImpl context = new LocaleResolverContextImpl();
        context.setConfiguration(configuration);
        context.setRequest(request);

        // candidates as sorted list
        List<LocaleResolver> candidates = StreamSupport.stream(resolvers.spliterator(), false)
                .sorted((resolver1, resolver2) -> {
                    final Priority prio1 = getAnnotation(resolver1.getClass(), Priority.class);
                    final Priority prio2 = getAnnotation(resolver2.getClass(), Priority.class);
                    final int value1 = prio1 != null ? prio1.value() : 1000;
                    final int value2 = prio2 != null ? prio2.value() : 1000;
                    return value2 - value1;
                })
                .collect(Collectors.toList());

        // do the resolving
        for (LocaleResolver candidate : candidates) {
            Locale locale = candidate.resolveLocale(context);
            if (locale != null) {
                return locale;
            }
        }

        throw new IllegalStateException("Could not resolve with any of the " + candidates.size()
                + " resolver implementations");

    }

    /**
     * It looks like {@link org.glassfish.ozark.util.AnnotationUtils#getAnnotation(Class, Class)}
     * still doesn't handle proxies correctly. This method handles proxies correctly
     * but unfortunately only works with Weld.
     */
    private <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        if (clazz.getName().endsWith("$$_WeldClientProxy")) {
            return clazz.getSuperclass().getAnnotation(annotationType);
        }
        return clazz.getAnnotation(annotationType);
    }

}
