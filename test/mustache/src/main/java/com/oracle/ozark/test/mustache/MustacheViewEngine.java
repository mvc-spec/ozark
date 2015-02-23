package com.oracle.ozark.test.mustache;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.ServletContext;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

/**
 * Class MustacheViewEngine.
 *
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class MustacheViewEngine implements ViewEngine {

	private static final String VIEW_BASE = "/WEB-INF/views/";

	@Inject
	private ServletContext servletContext;

	private MustacheFactory factory;

	public MustacheViewEngine() {
		factory = new CustomMustacheFactory();
	}

	@Override
	public boolean supports(String view) {
		return view.endsWith("mustache");
	}

	@Override
	public void processView(ViewEngineContext context) throws ViewEngineException {
		Mustache mustache = factory.compile(context.getView());
		try {
			Writer writer = context.getResponse().getWriter();
			mustache.execute(writer, context.getModels()).flush();
		} catch (IOException e) {
			throw new ViewEngineException(e);
		}
	}

	private class CustomMustacheFactory extends DefaultMustacheFactory {
		@Override
		public Reader getReader(String resourceName) {
			InputStream is = servletContext.getResourceAsStream(VIEW_BASE + resourceName);
			if (is != null) {
				return new BufferedReader(new InputStreamReader(is, UTF_8));
			}
			return super.getReader(resourceName);
		}
	}
}