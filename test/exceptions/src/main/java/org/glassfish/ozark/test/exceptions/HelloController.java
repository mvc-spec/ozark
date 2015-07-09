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
package org.glassfish.ozark.test.exceptions;

import javax.mvc.Controller;
import javax.mvc.View;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Tests various exception mapping cases for controllers.
 *
 * @author Santiago Pericas-Geertsen
 */
@Path("exceptions")
@Controller
public class HelloController {

    @GET
    @Path("not_found")
    @View("hello.jsp")
    public void notFound() {
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @GET
    @Path("not_found_no_view")
    public void notFoundNoView() {
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @GET
    @Path("internal_error")
    public Response internalError() {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("hello.jsp").build();
    }

    @GET
    @Path("internal_error_no_view")
    public Response internalErrorNoView() {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();
    }

    @GET
    @Path("internal_error_mapped")
    public void internalErrorMapped() {
        throw new ClientErrorException(Response.Status.BAD_REQUEST);
    }

    public static class GlobalExceptionMapper implements ExceptionMapper<ClientErrorException> {
        @Override
        public Response toResponse(ClientErrorException exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("hello.jsp").build();
        }
    }
}
