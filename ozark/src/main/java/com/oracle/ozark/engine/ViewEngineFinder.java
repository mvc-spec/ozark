package com.oracle.ozark.engine;

import com.oracle.ozark.core.Viewable;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.engine.Priorities;
import javax.mvc.engine.Supports;
import javax.mvc.engine.ViewEngine;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        // Gather set of candidates based on extensions
        final Set<ViewEngine> candidates = new HashSet<>();
        for (ViewEngine engine : engines) {
            final Supports sp = engine.getClass().getAnnotation(Supports.class);
            if (sp != null &&
                    Arrays.asList(sp.value()).stream().anyMatch(ext -> viewable.getView().endsWith(ext))) {
                candidates.add(engine);
            }
        }
        // Find candidate with highest priority
        Optional<ViewEngine> ve = candidates.stream().max(
                (e1, e2) -> {
                    final Priority p1 = e1.getClass().getAnnotation(Priority.class);
                    final int v1 = p1 != null ? p1.value() : Priorities.DEFAULT;
                    final Priority p2 = e2.getClass().getAnnotation(Priority.class);
                    final int v2 = p1 != null ? p2.value() : Priorities.DEFAULT;
                    return v1 - v2;
                });
        return ve.isPresent() ? ve.get() : null;        // TODO: caching
    }
}
