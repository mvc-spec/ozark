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
package org.glassfish.ozark.cdi;

import javax.mvc.annotation.RedirectScoped;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.CDI;

/**
 * The CDI context for RedirectScoped beans.
 * 
 * @author Manfred Riem (manfred.riem at oracle.com)
 */
public class RedirectScopeContext implements AlterableContext, Serializable {
    
    /**
     * Stores the serial version UID.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Stores the manager.
     */
    private RedirectScopeManager manager;

    /**
     * Destroy the given contextual.
     * 
     * @param contextual the contextual.
     */
    @Override
    public void destroy(Contextual<?> contextual) {
        getManager().destroy(contextual);
    }
    
    /**
     * Get the instance of a RedirectScoped bean.
     *
     * @param <T> the type.
     * @param contextual the contextual.
     * @return the view scoped bean, or null if not found.
     */
    @Override
    public <T> T get(Contextual<T> contextual) {
        return getManager().get(contextual);
    }

    /**
     * Get the instance of a RedirectScoped bean.
     * 
     * @param <T> the type.
     * @param contextual the contextual.
     * @param creational the creational.
     * @return the instance.
     * @throws ContextNotActiveException when the context is not active.
     */
    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creational) {
        return getManager().get(contextual, creational);
    }

    /**
     * Get the manager.
     * 
     * @return the manager.
     */
    public RedirectScopeManager getManager() {
        if (manager == null) {
            manager = CDI.current().select(RedirectScopeManager.class).get();
        }
        return manager;
    }

    /**
     * Get the class of the scope object.
     *
     * @return the class.
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return RedirectScoped.class;
    }

    /**
     * Is the scope active.
     * 
     * @return true if it is, false otherwise.
     */
    @Override
    public boolean isActive() {
        return true;
    }
}
