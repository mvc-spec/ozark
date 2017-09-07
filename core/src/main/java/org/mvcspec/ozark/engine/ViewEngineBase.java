/*
 * Copyright © 2017 Ivar Grimstad (ivar.grimstad@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mvcspec.ozark.engine;

import org.mvcspec.ozark.util.PathUtils;
import org.mvcspec.ozark.util.PropertyUtils;

import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;

/**
 * Base class for view engines that factors out all common logic.
 *
 * @author Santiago Pericas-Geertsen
 */
public abstract class ViewEngineBase implements ViewEngine {

    /**
     * Resolves a view path based on {@link javax.mvc.engine.ViewEngine#VIEW_FOLDER}
     * in the active configuration. If the view is absolute, starts with '/', then
     * it is returned unchanged.
     *
     * @param context view engine context.
     * @return resolved view.
     */
    protected String resolveView(ViewEngineContext context) {
        final String view = context.getView();
        if (!PathUtils.hasStartingSlash(view)) {        // Relative?
            final String viewFolder = PropertyUtils.getProperty(context.getConfiguration(), VIEW_FOLDER, DEFAULT_VIEW_FOLDER);
            return PathUtils.ensureEndingSlash(viewFolder) + view;
        }
        return view;
    }
}
