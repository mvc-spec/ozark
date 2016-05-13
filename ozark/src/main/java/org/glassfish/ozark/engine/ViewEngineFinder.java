package org.glassfish.ozark.engine;

import org.glassfish.ozark.util.CdiUtils;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.Viewable;
import javax.mvc.engine.Priorities;
import javax.mvc.engine.ViewEngine;
import java.util.*;

import static org.glassfish.ozark.util.AnnotationUtils.getAnnotation;

/**
 * <p>Selects the view engine for a {@link javax.mvc.Viewable}. If the viewable
 * includes a reference to an engine, the selection process stops and returns
 * it. Otherwise, the method {@link javax.mvc.engine.ViewEngine#supports(String)}
 * is called for each of the view engines injectable via CDI (i.e., all classes
 * that implement {@link javax.mvc.engine.ViewEngine}).</p>
 *
 * <p>The resulting set of candidates is sorted based on its priority as
 * defined by the annotation {@link javax.annotation.Priority} on the view engine
 * implementation.</p>
 *
 * <p>This class implements a simple cache to avoid repeated look-ups for the same
 * view.</p>
 *
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
public class ViewEngineFinder {

    @Inject
    private CdiUtils cdiUtils;

    private Map<String, ViewEngine> cache = new HashMap<>();

    /**
     * Finds view engine for a viewable.
     *
     * @param viewable the viewable to be used.
     * @return selected view engine or {@code null} if none found.
     */
    public ViewEngine find(Viewable viewable) {
        Optional<ViewEngine> engine;
        final String view = viewable.getView();

        // If engine specified in viewable, use it
        final Class<? extends ViewEngine> engineClass = viewable.getViewEngine();
        if (engineClass != null) {
            engine = Optional.of(cdiUtils.newBean(engineClass));
        } else {
            // Check cache first
            engine = Optional.ofNullable(cache.get(view));

            if (!engine.isPresent()) {
                // Gather set of candidates
                final Set<ViewEngine> candidates = new HashSet<>();

                List<ViewEngine> viewEngines = CdiUtils.getInstances(ViewEngine.class);

                for (ViewEngine viewEngine : viewEngines) {
                    if (viewEngine.supports(view)) {
                        candidates.add(viewEngine);
                    }
                }
                // Find candidate with highest priority
                engine = candidates.stream().max(
                        (e1, e2) -> {
                            final Priority p1 = getAnnotation(e1.getClass(), Priority.class);
                            final int v1 = p1 != null ? p1.value() : Priorities.DEFAULT;
                            final Priority p2 = getAnnotation(e2.getClass(), Priority.class);
                            final int v2 = p2 != null ? p2.value() : Priorities.DEFAULT;
                            return v1 - v2;
                        });
                // Update cache
                if (engine.isPresent()) {
                    cache.put(view, engine.get());
                }
            }
        }
        return engine.isPresent() ? engine.get() : null;
    }
}
