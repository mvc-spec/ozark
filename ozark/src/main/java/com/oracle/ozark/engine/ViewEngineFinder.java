package com.oracle.ozark.engine;

import com.oracle.ozark.core.Viewable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.engine.Supports;
import javax.mvc.engine.ViewEngine;
import java.util.Arrays;

/**
 * Class ViewEngineFinder.
 *
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
public class ViewEngineFinder {

    @Inject @Any
    private Instance<ViewEngine> engines;

    public ViewEngine find(Viewable viewable) {
        for (ViewEngine engine : engines) {
            final Supports sp = engine.getClass().getAnnotation(Supports.class);
            if (sp != null &&
                    Arrays.asList(sp.value()).stream().anyMatch(ext -> viewable.getView().endsWith(ext))) {
                return engine;      // TODO: caching?
            }
        }
        return null;
    }
}
