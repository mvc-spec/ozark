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
package com.oracle.ozark.engine;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.engine.Priorities;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Implementation of JSF Facelets view engine. Uses a {@link javax.servlet.RequestDispatcher}
 * to forward the request back to the servlet container.
 *
 * @author Manfred Riem
 * @author Santiago Pericas-Geertsen
 */
@Priority(Priorities.DEFAULT)
public class FaceletsViewEngine extends ViewEngineBase {

    @Inject
    private ServletContext servletContext;

    /**
     * Assumes that any view that ends with {@code xhtml} is a facelet.
     *
     * @param view the name of the view.
     * @return {@code true} if supported or {@code false} if not.
     */
    @Override
    public boolean supports(String view) {
        return view.endsWith("xhtml");
    }

    /**
     * Sets attributes in request based on {@link javax.mvc.Models} and forwards
     * request to servlet container.
     *
     * @param context view engine context.
     * @throws ViewEngineException if any error occurs.
     */
    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {
        final Models models = context.getModels();
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        for (String name : models) {
            request.setAttribute(name, models.get(name));
        }
        RequestDispatcher rd = servletContext.getRequestDispatcher(
                getViewFolder(context.getConfiguration()) + context.getView());
        try {
            rd.forward(request, response);
        } catch (ServletException | IOException e) {
            throw new ViewEngineException(e);
        }
    }
}
