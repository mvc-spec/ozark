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
package org.glassfish.ozark.ext.stringtemplate;

import org.glassfish.ozark.engine.ViewEngineBase;
import org.stringtemplate.v4.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.*;
import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

/**
 * Class StringTemplateViewEngine.
 *
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class StringTemplateViewEngine extends ViewEngineBase {

	@Inject
	private ServletContext servletContext;
	
    @Override
    public boolean supports(String view) {
        return view.endsWith(".st");
    }

	@Override
	public void processView(ViewEngineContext context) throws ViewEngineException {
		ST stringTemplate = getStringTemplate(resolveView(context));
		context.getModels().forEach((key, value) -> add(key, value, stringTemplate));
		try {
			PrintWriter writer = context.getResponse().getWriter();
			stringTemplate.write(new AutoIndentWriter(writer));
			stringTemplate.render();
		} catch (Exception e) {
			throw new ViewEngineException(e);
		}
	}

	private void add(String key, Object value, ST template) {
		if (template.getAttributes().containsKey(key)) template.add(key, value);
	}

	public ST getStringTemplate(String resolvedView) throws ViewEngineException {
		Matcher matcher = compile("(.+)/(.+)\\.st").matcher(resolvedView);
		if (matcher.find()) {
			String viewFolder = servletContext.getRealPath(matcher.group(1));
			String viewName = matcher.group(2);
			ST template = new STGroupDir(viewFolder, '$', '$').getInstanceOf(viewName);
			if (template != null) return template;
		}
		throw new ViewEngineException("Couldn't find view " + resolvedView);
	}
}