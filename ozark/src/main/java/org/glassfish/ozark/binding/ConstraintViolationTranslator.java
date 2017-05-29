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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Helper class to get a human readable errors message for a {@link ConstraintViolation}
 * for a specific target locale.
 *
 * @author Christian Kaltepoth
 */
@ApplicationScoped
public class ConstraintViolationTranslator {

    private static final Logger log = Logger.getLogger(ConstraintViolationTranslator.class.getName());

    /**
     * The ValidatorFactory should be available for injection as defined here:
     * http://beanvalidation.org/1.1/spec/#d0e11327
     */
    @Inject
    private Instance<ValidatorFactory> validatorFactoryInstance;

    /**
     * The actual ValidatorFactory to use
     */
    private ValidatorFactory validatorFactory;

    /**
     * We should be able to get a ValidatorFactory from the container in an Java EE environment.
     * However, if we don't get the factory, we will will use a default one. This is especially
     * useful for non Java EE environments and in the CDI tests
     */
    @PostConstruct
    public void init() {

        // Prefer the ValidatorFactory provided by the container
        Iterator<ValidatorFactory> iterator = validatorFactoryInstance.iterator();
        if (iterator.hasNext()) {
            this.validatorFactory = iterator.next();
        }

        // create a default factory if we didn't get one
        else {
            log.warning("Creating a ValidatorFactory because the container didn't provide one!");
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        }

    }

    /**
     * Returns the human readable error message for a given {@link ConstraintViolation}.
     *
     * @param violation The violation to get the message for
     * @param locale    The desired target locale
     * @return the localized message
     */
    public String translate(ConstraintViolation<?> violation, Locale locale) {

        SimpleMessageInterpolatorContext context = new SimpleMessageInterpolatorContext(violation);

        MessageInterpolator interpolator = validatorFactory.getMessageInterpolator();

        return interpolator.interpolate(violation.getMessageTemplate(), context, locale);

    }

    /**
     * Simple implementation of {@link javax.validation.MessageInterpolator.Context} wrapping
     * a {@link ConstraintViolation}.
     */
    private static class SimpleMessageInterpolatorContext implements MessageInterpolator.Context {

        private final ConstraintViolation<?> violation;

        public SimpleMessageInterpolatorContext(ConstraintViolation<?> violation) {
            this.violation = violation;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return violation.getConstraintDescriptor();
        }

        @Override
        public Object getValidatedValue() {
            return violation.getInvalidValue();
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            throw new UnsupportedOperationException();
        }
    }

}
