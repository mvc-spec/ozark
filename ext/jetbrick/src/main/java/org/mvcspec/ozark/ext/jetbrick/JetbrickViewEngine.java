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
package org.mvcspec.ozark.ext.jetbrick;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.ServletContext;

import org.mvcspec.ozark.engine.ViewEngineBase;

import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import jetbrick.template.TemplateException;
import jetbrick.template.web.JetWebEngine;

/**
 * @author Daniel Dias
 */
@ApplicationScoped
public class JetbrickViewEngine  extends ViewEngineBase {

	private  JetEngine jetEngine;

	@PostConstruct
	public void init() {
		jetEngine = JetWebEngine.create(servletContext);
	}

	@Inject
	private ServletContext servletContext;

	@Override
	public boolean supports(String view) {
		return view.endsWith(".jetx");
	}

	@Override
	public void processView(ViewEngineContext context) throws ViewEngineException {
		try {
			JetTemplate template = jetEngine.getTemplate(resolveView(context));
			Writer writer = context.getResponse().getWriter();
			template.render(context.getModels(), writer);
		} catch (TemplateException | IOException e) {
			throw new ViewEngineException(e);
		}
	}
}