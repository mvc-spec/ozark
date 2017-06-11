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
package org.glassfish.ozark.cdi;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.inject.Inject;
import javax.mvc.MvcContext;
import javax.mvc.binding.ValidationError;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import org.glassfish.ozark.binding.BindingResultImpl;
import org.glassfish.ozark.binding.ConstraintViolationTranslator;
import org.glassfish.ozark.binding.ConstraintViolationUtils;
import org.glassfish.ozark.binding.ValidationErrorImpl;

import static org.glassfish.ozark.binding.BindingResultUtils.updateBindingResultViolations;

/**
 * CDI backed interceptor to handle validation and binding issues.
 *
 * @author Dmytro Maidaniuk
 */
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
@MvcValidation
@Interceptor
public class ValidationInterceptor implements Serializable {

    private static final long serialVersionUID = -5804986456381504613L;

    private static final Logger LOG = Logger.getLogger(ValidationInterceptor.class.getName());

    @Inject
    Validator validator;

    @Inject
    private ConstraintViolationTranslator violationTranslator;

    @Inject
    private MvcContext mvcContext;

    @AroundInvoke
    public Object validateMethodInvocation(InvocationContext ctx) throws Exception {

        LOG.fine("Started execution of validation interceptor");
        ExecutableValidator executableValidator = validator.forExecutables();
        Object resource = ctx.getTarget();
        final BindingResultImpl bindingResult = null;
        Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(
                resource, ctx.getMethod(), ctx.getParameters());

        ConstraintViolationException cve;

        if (!violations.isEmpty()) {
            cve = new ConstraintViolationException(
                    getMessage(ctx.getMethod(), ctx.getParameters(), violations), violations);
            // Update binding result or re-throw exception if not present
            if (!updateBindingResultViolations(resource, buildViolationErrors(cve), bindingResult)) {
                throw cve;
            }
        }

        Object result = ctx.proceed();

        violations = executableValidator.validateReturnValue(ctx.getTarget(), ctx.getMethod(), result);

        if (!violations.isEmpty()) {
            cve = new ConstraintViolationException(
                    getMessage(ctx.getMethod(), ctx.getParameters(), violations), violations);
            // Update binding result or re-throw exception if not present
            if (!updateBindingResultViolations(resource, buildViolationErrors(cve), bindingResult)) {
                throw cve;
            }
        }

        return result;
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
                LOG.log(Level.WARNING, "Cannot resolve paramName for violation: {0}", violation);
            }

            String message = violationTranslator.translate(violation, mvcContext.getLocale());

            validationErrors.add(new ValidationErrorImpl(violation, paramName, message));

        }

        return validationErrors;

    }

    private String getMessage(Method method, Object[] args, Set<? extends ConstraintViolation<?>> violations) {

        StringBuilder message = new StringBuilder(400);
        message.append(violations.size());
        message.append(" constraint violation(s) occurred during method invocation.");
        message.append("\nMethod: ");
        message.append(method);
        message.append("\nArgument values: ");
        message.append(Arrays.toString(args));
        message.append("\nConstraint violations: ");

        int i = 1;
        for (ConstraintViolation<?> violation : violations) {
            message.append("\n (");
            message.append(i);
            message.append(") Message: ");
            message.append(violation.getMessage());

            i++;
        }

        return message.toString();
    }

}
