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
package com.oracle.ozark.mapper;

import com.oracle.ozark.cdi.CdiUtil;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.Viewable;
import javax.mvc.mapper.OnConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;

/**
 * Uses a Jersey SPI to check for the @OnConstraintViolation annotation on the resource
 * method. If not defined, it returns <code>false</code> in <code>isMappable</code>,
 * falling back to the default exception mapping mechanism defined in JAX-RS.
 *
 * @author Santiago Pericas-Geertsen
 */
public class ConstraintViolationMapper implements ExtendedExceptionMapper<ConstraintViolationException> {

    @Inject
    private CdiUtil cdiUtil;

    @Context
    private ResourceInfo resourceInfo;

    private String view;

    private javax.mvc.mapper.ConstraintViolationMapper mapper;

    /**
     * Default mapper for {@link javax.validation.ConstraintViolationException}. Bind
     * {@code ex} to the exception and return a 400 response using the view specified
     * in the annotation.
     */
    static private class DefaultMapper implements javax.mvc.mapper.ConstraintViolationMapper {

        private static final String EX_NAME = "ex";

        @Inject
        private Models models;

        @Override
        public Response toResponse(ConstraintViolationException e, String view) {
            models.put(EX_NAME, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(new Viewable(view)).build();
        }
    }

    /**
     * Determines if the exception is mappable by this mapper.
     *
     * @param exception the exception.
     * @return outcome of test.
     */
    @Override
    public boolean isMappable(ConstraintViolationException exception) {
        final Method method = resourceInfo.getResourceMethod();
        if (method != null) {
            OnConstraintViolation an = method.getAnnotation(OnConstraintViolation.class);
            if (an == null) {
                final Class<?> resourceClass = resourceInfo.getResourceClass();
                an = resourceClass.getAnnotation(OnConstraintViolation.class);
            }
            if (an != null) {
                Class<? extends javax.mvc.mapper.ConstraintViolationMapper> mapperClass = an.mapper();
                if (mapperClass == javax.mvc.mapper.ConstraintViolationMapper.class) {
                    mapperClass = DefaultMapper.class;      // use default
                }
                mapper = cdiUtil.newBean(mapperClass);
                if (mapper != null) {
                    view = an.view();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Maps the exception by forwarding it to the MVC application mapper.
     *
     * @param e exception to be mapped.
     * @return a response mapped from the exception.
     */
    @Override
    public Response toResponse(ConstraintViolationException e) {
        return mapper.toResponse(e, view);
    }
}
