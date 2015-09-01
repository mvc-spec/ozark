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
package org.glassfish.ozark.binding;

import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.ValidationInterceptor;
import org.glassfish.jersey.server.spi.ValidationInterceptorContext;

import javax.enterprise.context.ApplicationScoped;
import javax.mvc.binding.BindingError;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Set;

import static org.glassfish.ozark.binding.BindingResultUtils.getValidInstanceForType;
import static org.glassfish.ozark.binding.BindingResultUtils.updateBindingResultErrors;
import static org.glassfish.ozark.binding.BindingResultUtils.updateBindingResultViolations;

/**
 * CDI backed interceptor to handle validation and binding issues.
 *
 * @author Santiago Pericas-Geertsen
 * @author Jakub Podlesak
 */
@ApplicationScoped
public class BindingInterceptorImpl implements ValidationInterceptor {

    @Override
    public void onValidate(ValidationInterceptorContext ctx) throws ValidationException {
        final Object[] args = ctx.getArgs();

        // Unwrap if necessary
        Object resource = ctx.getResource();
        if (BindingResultUtils.isTargetInstanceProxy(resource)) {
            resource = BindingResultUtils.getTargetInstance(resource);
            ctx.setResource(resource);
        }

        // If any of the args is a RuntimeException, collect and report errors
        final BindingResultImpl bindingResult = getBindingResultInArgs(args);
        RuntimeException firstException = null;
        final Set<BindingError> errors = new HashSet<>();
        final Parameter[] paramsInfo = ctx.getInvocable().getParameters().toArray(new Parameter[0]);

        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            if (arg instanceof RuntimeException) {
                final Parameter paramInfo = paramsInfo[i];
                final RuntimeException ex = ((RuntimeException) arg);
                errors.add(new BindingErrorImpl(ex.getCause().toString(), paramInfo.getSourceName()));
                if (firstException == null) {
                    firstException = ex;
                }
                // Replace parameter with a valid instance or null
                args[i] = getValidInstanceForType(paramInfo.getRawType());
            }
        }

        // Update binding result or re-throw first exception if not present
        if (errors.size() > 0) {
            if (!updateBindingResultErrors(resource, errors, bindingResult)) {
                throw firstException;
            }
        }

        try {
            ctx.proceed();
        } catch (ConstraintViolationException cve) {
            // Update binding result or re-throw exception if not present
            if (!updateBindingResultViolations(resource, cve.getConstraintViolations(), bindingResult)) {
                throw cve;
            }
        }
    }

    /**
     * Finds the first argument of type {@code org.glassfish.ozark.binding.BindingResultImpl}.
     * Inspects superclasses in case of proxies.
     *
     * @param args list of arguments to search.
     * @return argument found or {@code null}.
     */
    private BindingResultImpl getBindingResultInArgs(Object[] args) {
        for (Object a : args) {
            if (a != null) {
                Class<?> argClass = a.getClass();
                do {
                    if (BindingResultImpl.class.isAssignableFrom(argClass)) {
                        return (BindingResultImpl) a;
                    }
                    argClass = argClass.getSuperclass();
                } while (argClass != Object.class);
            }
        }
        return null;
    }
}

