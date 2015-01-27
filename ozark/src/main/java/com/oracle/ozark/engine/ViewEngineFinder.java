package com.oracle.ozark.engine;

import com.oracle.ozark.cdi.CdiUtil;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.Viewable;
import javax.mvc.engine.Priorities;
import javax.mvc.engine.ViewEngine;
import javax.mvc.event.ViewEngineSelected;
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

    @Inject
    @Any
    private Instance<ViewEngine> engines;

    @Inject
    private Event<ViewEngineSelected> selectedEvent;

    @Inject
    private ViewEngineSelected selected;

    @Inject
    private Instance<CdiUtil> cdiUtil;

    public ViewEngine find(Viewable viewable) {
        Optional<ViewEngine> engine;

        // If engine specified in viewable, use it
        final Class<? extends ViewEngine> engineClass = viewable.getViewEngine();
        if (engineClass != null) {
            engine = Optional.of(cdiUtil.get().newBean(engineClass));
        } else {
            // Gather set of candidates based on extensions
            final Set<ViewEngine> candidates = new HashSet<>();
            for (ViewEngine e : engines) {
                if (e.supports(viewable.getView())) {
                    candidates.add(e);
                }
            }
            // Find candidate with highest priority
            engine = candidates.stream().max(
                    (e1, e2) -> {
                        final Priority p1 = e1.getClass().getAnnotation(Priority.class);
                        final int v1 = p1 != null ? p1.value() : Priorities.DEFAULT;
                        final Priority p2 = e2.getClass().getAnnotation(Priority.class);
                        final int v2 = p1 != null ? p2.value() : Priorities.DEFAULT;
                        return v1 - v2;
                    });
        }
        if (engine.isPresent()) {
            // Fire ViewEngineSelected event
            selected.setView(viewable.getView());
            selected.setEngine(engine.get().getClass());
            selectedEvent.fire(selected);
            return engine.get();        // TODO: caching?
        }
        return null;
    }
}
