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
