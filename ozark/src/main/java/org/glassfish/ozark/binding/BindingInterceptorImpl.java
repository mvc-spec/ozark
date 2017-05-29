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
package org.glassfish.ozark.binding;

import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.ValidationInterceptor;
import org.glassfish.jersey.server.spi.ValidationInterceptorContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.MvcContext;
import javax.mvc.binding.BindingError;
import javax.mvc.binding.ValidationError;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

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

    private static final Logger LOG = Logger.getLogger(BindingInterceptorImpl.class.getName());

    @Inject
    private ConstraintViolationTranslator violationTranslator;

    @Inject
    private MvcContext mvcContext;

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
        if (errors.size() > 0 && !updateBindingResultErrors(resource, errors, bindingResult)) {
            throw firstException;
        }

        try {
            ctx.proceed();
        } catch (ConstraintViolationException cve) {
            // Update binding result or re-throw exception if not present
            if (!updateBindingResultViolations(resource, buildViolationErrors(cve), bindingResult)) {
                throw cve;
            }
        }
    }

    /**
     * Creates a set of violation errors from a {@link ConstraintViolationException}.
     *
     * @param cve the exception containing the violations
     * @return the set of validation errors
     */
    private Set<ValidationError> buildViolationErrors(ConstraintViolationException cve) {

        Set<ValidationError> validationErrors = new LinkedHashSet<>();

        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {

            String paramName = ConstraintViolationUtils.getParamName(violation);
            if (paramName == null) {
                LOG.warning("Cannot resolve paramName for violation: " + violation);
            }

            String message = violationTranslator.translate(violation, mvcContext.getLocale());

            validationErrors.add(new ValidationErrorImpl(violation, paramName, message));

        }

        return validationErrors;

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

