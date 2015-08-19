/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.ozark.ext.jade;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.exceptions.JadeException;
import org.glassfish.ozark.engine.ViewEngineBase;
import org.glassfish.ozark.engine.ViewEngineConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import java.io.IOException;

/**
 * The Jade View Engine.
 *
 * @author Florian Hirsch
 * @see <a href="http://jade-lang.com/">Jade</a>
 * @see <a href="https://github.com/neuland/jade4j">Jade4J</a>
 */
@ApplicationScoped
public class JadeViewEngine extends ViewEngineBase {

    @Inject
    @ViewEngineConfig
    private JadeConfiguration jade;

    @Override
    public boolean supports(String view) {
        return view.endsWith(".jade");
    }

    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {
        String viewPath = resolveView(context);
        try {
            jade.renderTemplate(jade.getTemplate(viewPath), context.getModels(), context.getResponse().getWriter());
        } catch (JadeException | IOException ex) {
            throw new ViewEngineException(String.format("Could not process view %s.", context.getView()), ex);
        }
    }
}
