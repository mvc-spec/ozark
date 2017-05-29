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

import javax.mvc.binding.ValidationError;
import javax.validation.ConstraintViolation;

/**
 * Implementation of the {@link ValidationError} interface.
 *
 * @author Christian Kaltepoth
 */
public class ValidationErrorImpl implements ValidationError {

    private final ConstraintViolation<?> violation;
    private final String param;
    private final String message;

    public ValidationErrorImpl(ConstraintViolation<?> violation, String param, String message) {
        this.violation = violation;
        this.param = param;
        this.message = message;
    }

    @Override
    public String getParamName() {
        return param;
    }

    @Override
    public ConstraintViolation<?> getViolation() {
        return violation;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
