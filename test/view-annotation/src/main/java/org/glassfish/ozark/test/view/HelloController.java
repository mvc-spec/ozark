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
package org.glassfish.ozark.test.view;

import javax.mvc.Controller;
import javax.mvc.View;
import javax.mvc.Viewable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Locale;

/**
 * Tests uses of {@code View} annotation with void and non-void methods. If a method
 * decorated with {@code View} returns {@code null}, the value in {@code View} should
 * be used.
 *
 * @author Santiago Pericas-Geertsen
 */
@Path("/")
@Controller
@View("hello.jsp")
public class HelloController {

    /**
     * Void method with @View annotation. View 'hello.jsp' should be rendered.
     */
    @GET
    @Path("void")
    @View("hello.jsp")
    public void voidController() {
    }

    /**
     * Method that overrides the @View annotation. View 'bye.jsp' should be
     * rendered.
     *
     * @return View to render.
     */
    @GET
    @Path("string")
    @View("hello.jsp")
    public String byeController() {
        return "bye.jsp";
    }

    /**
     * Method that returns a null value. View 'hello.jsp' should be rendered.
     *
     * @return View to render.
     */
    @GET
    @Path("null")
    @View("hello.jsp")
    public String nullController() {
        return null;
    }

    /**
     * Void method with @View annotation from class. View 'hello.jsp' should be
     * rendered.
     */
    @GET
    @Path("class/void")
    public void voidControllerClass() {
    }

    /**
     * Method that overrides the @View annotation from class. View 'bye.jsp' should
     * be rendered.
     *
     * @return View to render.
     */
    @GET
    @Path("class/string")
    public String byeControllerClass() {
        return "bye.jsp";
    }

    /**
     * Method that returns a null value. View 'hello.jsp' from class should be rendered.
     *
     * @return View to render.
     */
    @GET
    @Path("class/null")
    public String nullControllerClass() {
        return null;
    }
}
