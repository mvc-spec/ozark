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
package org.mvcspec.ozark.ext.thymeleaf;

import org.mvcspec.ozark.engine.ViewEngineBase;
import org.mvcspec.ozark.engine.ViewEngineConfig;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class Thymeleaf ViewEngine.
 *
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class ThymeleafViewEngine extends ViewEngineBase {

	@Inject
	private ServletContext servletContext;

	@Inject
	@ViewEngineConfig
	private TemplateEngine engine;

	@Override
	public boolean supports(String view) {
		return view.endsWith(".html");
	}

	@Override
	public void processView(ViewEngineContext context) throws ViewEngineException {
		
		try {
			
			HttpServletRequest request = context.getRequest(HttpServletRequest.class);
			HttpServletResponse response = context.getResponse(HttpServletResponse.class);
			WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

			Map<String, Object> model = new HashMap<>(context.getModels().asMap());
			model.put("request", request);
			ctx.setVariables(model);
			
			engine.process(resolveView(context), ctx, response.getWriter());
			
		} catch (IOException e) {
			throw new ViewEngineException(e);
		}
	}
}
