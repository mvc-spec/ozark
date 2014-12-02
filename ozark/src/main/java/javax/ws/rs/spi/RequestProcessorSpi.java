package javax.ws.rs.spi;

import java.io.OutputStream;

/**
 * Service provider interface to process an HTTP request after it has been matched by
 * JAX-RS. This extension point enables re-usability of the matching and binding
 * components in JAX-RS. A resource class should be identified as a target
 * for a specific SPI, for example, via the use of a special annotation or qualifier.
 *
 * @author Santiago Pericas-Geertsen
 */
public interface RequestProcessorSpi {

    /**
     * Called after a method has been matched.
     *
     * TODO: Could methodName be a method reference instead? No base type for method refs.
     *
     * @param resource   Instance of resource class chosen. Must be a CDI bean.
     * @param methodName Name of method matched.
     * @param params     Array of actual param values to call method. These are injected
     *                   values like @PathParam, etc.
     * @param os         Output stream used to produce response by service provider.
     * @return True if method was processed by service provider, false otherwise.
     */
    boolean matched(Object resource, String methodName, Object[] params, OutputStream os);
}

