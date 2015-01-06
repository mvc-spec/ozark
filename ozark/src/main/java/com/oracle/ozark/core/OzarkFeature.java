package com.oracle.ozark.core;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Class OzarkFeature.
 *
 * @author Santiago Pericas-Geertsen
 */
public class OzarkFeature implements Feature {

    @Override
    public boolean configure(FeatureContext featureContext) {
        featureContext.register(StringWriterInterceptor.class);
        return true;
    }
}
