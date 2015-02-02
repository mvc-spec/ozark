package com.oracle.ozark.freemarker;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class FreemarkerViewEngine.
 *
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
public class FreemarkerViewEngine implements ViewEngine {

    private static final String VIEW_BASE = "/WEB-INF/";

    @Inject
    private ServletContext servletContext;

    private Configuration configuration;

    public FreemarkerViewEngine() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateLoader(new TemplateLoader() {

            @Override
            public Object findTemplateSource(String s) throws IOException {
                return servletContext.getResourceAsStream(VIEW_BASE + s);
            }

            @Override
            public long getLastModified(Object o) {
                return -1;
            }

            @Override
            public Reader getReader(Object o, String s) throws IOException {
                return new InputStreamReader((InputStream) o);
            }

            @Override
            public void closeTemplateSource(Object o) throws IOException {
                ((InputStream) o).close();
            }
        });
    }

    @Override
    public boolean supports(String view) {
        return view.endsWith("ftl");
    }

    @Override
    public void processView(ViewEngineContext context) throws ServletException, IOException {
        final Template template = configuration.getTemplate(context.getView());
        try {
            template.process(context.getModels(),
                    new OutputStreamWriter(context.getResponse().getOutputStream()));
        } catch (TemplateException e) {
            throw new RuntimeException(e);      // TODO
        }
    }
}
