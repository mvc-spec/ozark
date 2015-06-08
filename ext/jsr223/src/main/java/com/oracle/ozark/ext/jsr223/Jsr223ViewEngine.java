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
package com.oracle.ozark.ext.jsr223;

import com.oracle.ozark.engine.ViewEngineBase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.enterprise.context.ApplicationScoped;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * The JSR-223 ViewEngine.
 *
 * @author Manfred Riem (manfred.riem@oracle.com)
 */
@ApplicationScoped
public class Jsr223ViewEngine extends ViewEngineBase {

    /**
     * Stores our global ScriptEngineManager.
     */
    ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    /**
     * What extensions does the view engine support.
     *
     * @param view the view.
     * @return if we support the extension or not.
     */
    @Override
    public boolean supports(String view) {
        return getScriptEngine(view) != null;
    }
    
    /**
     * Get the script engine by extension.
     * 
     * @param view the view.
     * @return the script engine, or null if not found.
     */
    private ScriptEngine getScriptEngine(String view) {
        if (view.contains(".")) {
            String extension = view.substring(view.lastIndexOf(".") + 1);
            return scriptEngineManager.getEngineByExtension(extension);
        }
        return null;
    }

    /**
     * Process the view.
     *
     * @param context the context.
     * @throws ViewEngineException when the view engine experienced an error.
     */
    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {

        ScriptEngine scriptEngine = getScriptEngine(context.getView());
        Object responseObject;
        try {
            InputStream inputStream = context.getRequest()
                    .getServletContext().getResourceAsStream(resolveView(context));
            InputStreamReader reader = new InputStreamReader(inputStream);
            Bindings bindings = scriptEngine.createBindings();
            bindings.put("models", context.getModels());
            responseObject = scriptEngine.eval(reader, bindings);
        } catch (ScriptException exception) {
            throw new ViewEngineException("Unable to execute script", exception);
        }

        try {
            context.getResponse().getWriter().print(responseObject.toString());
        } catch (IOException exception) {
            throw new ViewEngineException("Unable to write response", exception);
        }
    }
}
