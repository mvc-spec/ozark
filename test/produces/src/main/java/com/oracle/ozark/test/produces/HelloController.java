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
package com.oracle.ozark.test.produces;

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
 * HelloController test.
 *
 * @author Santiago Pericas-Geertsen
 */
@Path("/")
@Controller
public class HelloController {

    /**
     * Default content type should be text/html.
     */
    @GET
    @Path("no_produces1")
    public String noProduces1() {
        return "hello.jsp";
    }

    /**
     * Default content type should be text/html when using @View.
     */
    @GET
    @Path("no_produces2")
    @View("hello.jsp")
    public void noProduces2() {
    }

    /**
     * If HTML and XHTML are equally preferred, the latter should be chosen
     * given the lower qs on the former.
     */
    @GET
    @Produces({"text/html;qs=0.9", "application/xhtml+xml"})
    @Path("multiple_produces1")
    public String multipleProduces1() {
        return "hello.jsp";
    }

    /**
     * If HTML and XHTML are equally preferred, the latter should be chosen
     * given the lower qs on the former when using @View.
     */
    @GET
    @Produces({"text/html;qs=0.9", "application/xhtml+xml"})
    @Path("multiple_produces2")
    @View("hello.jsp")
    public void multipleProduces2() {
    }

    /**
     * XHTML type should override static HTML one in @Produces in this case.
     */
    @GET
    @Path("other_produces1")
    @Produces("text/html")      // overridden below
    public Response otherProduces1() {
        return Response.ok(new Viewable("hello.jsp"), "application/xhtml+xml").build();
    }

    /**
     * XHTML type should override static HTML one in @Produces in this case
     * when using @View.
     */
    @GET
    @Path("other_produces2")
    @Produces("text/html")      // overridden below
    @View("hello.jsp")
    public Response otherProduces2() {
        return Response.ok(new Viewable("hello.jsp"), "application/xhtml+xml").build();
    }

    /**
     * Sets language to "es".
     */
    @GET
    @Path("language1")
    @Produces("text/html")      // overridden below
    public Response language1() {
        return Response.ok(new Viewable("hello.jsp"), "application/xhtml+xml").language("es").build();
    }

    /**
     * Sets language to "es" when using @View.
     */
    @GET
    @Path("language2")
    @Produces("text/html")      // overridden below
    @View("hello.jsp")
    public Response language2() {
        return Response.ok(new Viewable("hello.jsp"), "application/xhtml+xml").language("es").build();
    }

    /**
     * Sets locale to UK.
     */
    @GET
    @Path("locale1")
    @Produces("text/html")      // overridden below
    public Response locale1() {
        return Response.ok(new Viewable("hello.jsp"), "application/xhtml+xml").language(Locale.UK).build();
    }

    /**
     * Sets locale to UK when using @View.
     */
    @GET
    @Path("locale2")
    @Produces("text/html")      // overridden below
    @View("hello.jsp")
    public Response locale2() {
        return Response.ok(new Viewable("hello.jsp"), "application/xhtml+xml").language(Locale.UK).build();
    }
}
