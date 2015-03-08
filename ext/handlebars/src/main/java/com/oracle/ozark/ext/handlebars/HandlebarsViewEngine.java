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
package com.oracle.ozark.ext.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.BlockHelper;
import com.github.jknack.handlebars.helper.EachHelper;
import com.github.jknack.handlebars.helper.EmbeddedHelper;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.engine.Priorities;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.*;
import java.io.BufferedReader;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.io.PrintWriter;import java.lang.Override;import java.lang.String;import java.util.*;
import java.util.stream.Collectors;

/**
 * Class HandlebarsViewEngine
 * @author Rahman Usta
 */
@ApplicationScoped
public class HandlebarsViewEngine implements ViewEngine {

    @Inject
    private ServletContext servletContext;

    @Override
    public boolean supports(String view) {
        return view.endsWith(".hbs");
    }

    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {

        Models models = context.getModels();
        String viewName = context.getView();
        if (!viewName.startsWith("/"))
            viewName = "/" + viewName;

        try (PrintWriter writer = context.getResponse().getWriter();
             InputStream resourceAsStream = servletContext.getResourceAsStream(viewName);
             InputStreamReader in = new InputStreamReader(resourceAsStream, "UTF-8");
             BufferedReader bufferedReader = new BufferedReader(in);) {

            String viewContent = bufferedReader.lines().collect(Collectors.joining());

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline(viewContent);
            template.apply(models, writer);

        } catch (IOException e) {
            throw new ViewEngineException(e);
        }

    }
}
