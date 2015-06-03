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
package com.oracle.ozark.jersey;

import com.oracle.ozark.security.CsrfProtectFilter;
import com.oracle.ozark.security.CsrfValidateInterceptor;
import com.oracle.ozark.core.ViewResponseFilter;
import com.oracle.ozark.core.ViewableWriter;
import com.oracle.ozark.validation.ValidationInterceptorImpl;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;

import javax.annotation.Priority;
import javax.mvc.Controller;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.FeatureContext;
import java.util.Arrays;

import static com.oracle.ozark.util.AnnotationUtils.getAnnotation;

/**
 * <p>Jersey feature that sets up the JAX-RS pipeline for MVC processing using one
 * or more providers. This feature is enabled only if any of the classes or methods
 * in the application has an instance of the {@link javax.mvc.Controller} annotation.</p>
 *
 * <p>Takes advantage of the {@link org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable}
 * SPI in Jersey.</p>
 *
 * @author Santiago Pericas-Geertsen
 */
@ConstrainedTo(RuntimeType.SERVER)
@Priority(AutoDiscoverable.DEFAULT_PRIORITY)
public class OzarkFeature implements ForcedAutoDiscoverable {

    @Override
    public void configure(FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (config.isRegistered(ViewResponseFilter.class)) {
            return;     // already registered!
        }
        final boolean enableOzark = config.getClasses().stream().anyMatch(this::isController)
                || config.getInstances().stream().map(o -> o.getClass()).anyMatch(this::isController);
        if (enableOzark) {
            context.register(ViewResponseFilter.class);
            context.register(ViewableWriter.class);
            context.register(ValidationInterceptorImpl.class);
            context.register(OzarkModelProcessor.class);
            context.register(CsrfValidateInterceptor.class);
            context.register(CsrfProtectFilter.class);
        }
    }

    private boolean isController(Class<?> c) {
        return getAnnotation(c, Controller.class) != null ||
                Arrays.asList(c.getMethods()).stream().anyMatch(m -> getAnnotation(m, Controller.class) != null);
    }
}
