package com.oracle.ozark.engine;

import com.oracle.ozark.cdi.CdiUtil;
import com.oracle.ozark.event.ViewEngineSelected;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mvc.Viewable;
import javax.mvc.engine.Priorities;
import javax.mvc.engine.ViewEngine;
import java.util.*;

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

    private Map<String, ViewEngine> cache = new HashMap<>();

    public ViewEngine find(Viewable viewable) {
        Optional<ViewEngine> engine;
        final String view = viewable.getView();

        // If engine specified in viewable, use it
        final Class<? extends ViewEngine> engineClass = viewable.getViewEngine();
        if (engineClass != null) {
            engine = Optional.of(cdiUtil.get().newBean(engineClass));
        } else {
            // Check cache first
            engine = Optional.ofNullable(cache.get(view));

            if (!engine.isPresent()) {
                // Gather set of candidates
                final Set<ViewEngine> candidates = new HashSet<>();
                for (ViewEngine e : engines) {
                    if (e.supports(view)) {
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
                // Update cache
                if (engine.isPresent()) {
                    cache.put(view, engine.get());
                }
            } else {
                selected.setCached(true);
            }
        }
        if (engine.isPresent()) {
            // Fire ViewEngineSelected event
            selected.setView(view);
            selected.setEngine(engine.get().getClass());
            selectedEvent.fire(selected);
            return engine.get();
        }
        return null;
    }
}
