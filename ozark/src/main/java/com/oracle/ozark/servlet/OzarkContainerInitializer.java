package com.oracle.ozark.servlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.ws.rs.Path;
import java.util.Set;

/**
 * Class OzarkContainerInitializer.
 *
 * @author Santiago Pericas-Geertsen
 */
@HandlesTypes(Path.class)
public class OzarkContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        // no-op
    }
}
