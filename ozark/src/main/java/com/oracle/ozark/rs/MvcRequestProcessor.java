package com.oracle.ozark.rs;

import javax.ws.rs.spi.RequestProcessorSpi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.OutputStream;

import static java.util.Arrays.*;

/**
 * MVC request processor to hook up in the JAX-RS runtime.
 *
 * @author Santiago Pericas-Geertsen
 */
public class MvcRequestProcessor implements RequestProcessorSpi {

    @Override
    public boolean matched(Object resource, String methodName, Object[] params, OutputStream os) {
        final Class<?> c = resource.getClass();
        try {
            final Method m = c.getMethod(methodName,
                    (Class<?>[]) asList(params).stream().map(p -> p.getClass()).toArray());
            final Object result = m.invoke(resource, params);

            // Process result according to MVC semantics

            // Write output to OutputStream parameter

            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}

