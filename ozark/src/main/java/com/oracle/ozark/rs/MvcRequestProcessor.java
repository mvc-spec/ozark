package com.oracle.ozark.rs;

import javax.ws.rs.spi.RequestProcessorSpi;
import java.io.OutputStream;

/**
 * MVC request processor to hook up in the JAX-RS runtime.
 *
 * @author Santiago Pericas-Geertsen
 */
public class MvcRequestProcessor implements RequestProcessorSpi {

    @Override
    public boolean matched(Object resource, String methodName, Object[] params, OutputStream os) {
        return false;
    }
}
