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
package org.glassfish.ozark.jersey;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;
import org.glassfish.ozark.MvcContextImpl;
import org.glassfish.ozark.binding.BindingInterceptorImpl;
import org.glassfish.ozark.core.ViewRequestFilter;
import org.glassfish.ozark.core.ViewResponseFilter;
import org.glassfish.ozark.core.ViewableWriter;
import org.glassfish.ozark.security.CsrfProtectFilter;
import org.glassfish.ozark.security.CsrfValidateInterceptor;
import org.glassfish.ozark.util.AnnotationUtils;

import javax.annotation.Priority;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.faces.bean.ManagedBean;
import javax.mvc.annotation.Controller;
import javax.servlet.ServletContext;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.glassfish.ozark.util.AnnotationUtils.getAnnotation;
import static org.glassfish.ozark.util.CdiUtils.newBean;

/**
 * <p>Jersey feature that sets up the JAX-RS pipeline for MVC processing using one
 * or more providers. This feature is enabled only if any of the classes or methods
 * in the application has an instance of the {@link javax.mvc.annotation.Controller} annotation.</p>
 *
 * <p>Takes advantage of the {@link org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable}
 * SPI in Jersey.</p>
 *
 * @author Santiago Pericas-Geertsen
 */
@ConstrainedTo(RuntimeType.SERVER)
@Priority(AutoDiscoverable.DEFAULT_PRIORITY)
public class OzarkFeature implements ForcedAutoDiscoverable {

    private static final Logger LOG = Logger.getLogger(OzarkFeature.class.getName());

    private static final List<Class<? extends Annotation>> UNSUPPORTED_TYPES = Arrays.asList(Stateless.class, Stateful.class, ManagedBean.class);

    @Context
    private ServletContext servletContext;

    @Override
    public void configure(FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (config.isRegistered(ViewResponseFilter.class)) {
            return;     // already registered!
        }
        Set<Class<?>> controllers = Stream.concat(config.getClasses().stream(), config.getInstances().stream().map(o -> o.getClass()))
                .filter(this::isController).collect(Collectors.toSet());
        if (controllers.isEmpty() || hasUnsupportedTypes(controllers)) {
            return;
        }
        context.register(ViewRequestFilter.class);
        context.register(ViewResponseFilter.class);
        context.register(ViewableWriter.class);
        context.register(BindingInterceptorImpl.class);
        context.register(OzarkModelProcessor.class);
        context.register(CsrfValidateInterceptor.class);
        context.register(CsrfProtectFilter.class);

        // Initialize application config object in Mvc class
        final BeanManager bm = CDI.current().getBeanManager();
        final MvcContextImpl mvc = newBean(bm, MvcContextImpl.class);
        mvc.setConfig(config);
        mvc.setContextPath(servletContext.getContextPath());
    }

    boolean isController(Class<?> c) {
        return getAnnotation(c, Controller.class) != null ||
                Arrays.asList(c.getMethods()).stream().anyMatch(m -> getAnnotation(m, Controller.class) != null);
    }

    /**
     * <p>MVC classes are required to be CDI-managed beans only.
     * Managed Beans or EJBs are not allowed (MCV-Spec ch. 2.1.1).</p>
     */
    boolean hasUnsupportedTypes(Set<Class<?>> controllers) {
        Set<Class<?>> unsupportedControllers = controllers.stream()
                .filter(bean -> UNSUPPORTED_TYPES.stream().anyMatch(type -> AnnotationUtils.hasAnnotation(bean, type)))
                .collect(Collectors.toSet());
        if (unsupportedControllers.isEmpty()) {
            return false;
        }
        // Jersey will swallow every Exception so we'll at least log an error.
        LOG.severe(String.format(
                "MVC controllers are required to be CDI-managed beans only. EJBs or ManagedBeans are not allowed. Unsupported annotations were found on: %s",
                unsupportedControllers));
        return true;
    }

}
