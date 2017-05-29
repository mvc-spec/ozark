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
package org.glassfish.ozark.test.validation;

import javax.inject.Inject;
import javax.mvc.annotation.Controller;
import javax.mvc.binding.BindingResult;
import javax.mvc.binding.ValidationError;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * FormController class. Defines ValidationResult as a property inherited
 * from a base class.
 *
 * @author Santiago Pericas-Geertsen
 */
@Controller
@Path("formprop")
@Produces("text/html")
public class FormControllerProperty extends FormControllerBase {

    @Inject
    private ErrorDataBean error;

    @POST
    @ValidateOnExecution(type = ExecutableType.NONE)
    public Response formPost(@Valid @BeanParam FormDataBean form) {
        final BindingResult vr = getVr();
        if (vr.isFailed()) {
            ValidationError validationError = vr.getAllValidationErrors().iterator().next();
            final ConstraintViolation<?> cv = validationError.getViolation();
            final String property = cv.getPropertyPath().toString();
            error.setProperty(property.substring(property.lastIndexOf('.') + 1));
            error.setValue(cv.getInvalidValue());
            error.setMessage(cv.getMessage());
            error.setParam(validationError.getParamName());
            return Response.status(BAD_REQUEST).entity("error.jsp").build();
        }
        return Response.status(OK).entity("data.jsp").build();
    }
}
