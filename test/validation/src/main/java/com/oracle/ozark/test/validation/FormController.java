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
package com.oracle.ozark.test.validation;

import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Viewable;
import javax.mvc.mapper.ConstraintViolationMapper;
import javax.mvc.mapper.OnConstraintViolation;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * FormController class.
 *
 * @author Santiago Pericas-Geertsen
 */
@Controller
@Path("form")
@Produces("text/html")
public class FormController {

    @POST
    @OnConstraintViolation(view="error.jsp", mapper=FormViolationMapper.class)
    public String get(@Valid @BeanParam FormDataBean form) {
        return "data.jsp";
    }

    public static class FormViolationMapper implements ConstraintViolationMapper {

        @Inject
        private ErrorDataBean error;

        @Override
        public Response toResponse(ConstraintViolationException e, String view) {
            final Set<ConstraintViolation<?>> set = e.getConstraintViolations();
            if (!set.isEmpty()) {
                final ConstraintViolation<?> cv = set.iterator().next();
                final String property = cv.getPropertyPath().toString();
                error.setProperty(property.substring(property.lastIndexOf('.') + 1));
                error.setValue(cv.getInvalidValue());
                error.setMessage(cv.getMessage());
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(view).build();
        }
    }
}
