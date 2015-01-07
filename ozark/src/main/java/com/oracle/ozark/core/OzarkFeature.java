package com.oracle.ozark.core;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Class OzarkFeature.
 *
 * @author Santiago Pericas-Geertsen
 */
@ConstrainedTo(RuntimeType.SERVER)
public class OzarkFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (!config.isRegistered(StringWriterInterceptor.class)) {
            context.register(StringWriterInterceptor.class);
            return true;
        }
        return false;
    }
}
