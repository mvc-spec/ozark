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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;
import javax.mvc.event.AfterProcessViewEvent;
import javax.mvc.event.BeforeControllerEvent;
import javax.mvc.event.ControllerRedirectEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

/**
 * The ApplicationScoped redirect scope manager.
 *
 * @author Manfred Riem (manfred.riem at oracle.com)
 */
@ApplicationScoped
public class RedirectScopeManager {

    private static final String PREFIX = "org.glassfish.ozark.redirect.";
    private static final String SCOPE_ID = PREFIX + "scopeId";
    private static final String INSTANCE = "Instance-";
    private static final String CREATIONAL = "Creational-";

    /**
     * Stores the beanManager.
     */
    @Inject
    BeanManager beanManager;

    /**
     * Stores the HTTP servlet request we are working for.
     */
    @Inject
    private HttpServletRequest request;

    /**
     * Stores the HTTP servlet response we work for.
     */
    @Context
    private HttpServletResponse response;
    
    /**
     * Destroy the instance.
     *
     * @param contextual the contextual.
     */
    public void destroy(Contextual contextual) {
        String scopeId = (String) request.getAttribute(SCOPE_ID);
        if (null != scopeId) {
            HttpSession session = request.getSession();
            PassivationCapable pc = (PassivationCapable) contextual;
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(scopeId);
            if (null != scopeMap) {
                Object instance = scopeMap.get(INSTANCE + pc.getId());
                CreationalContext<?> creational = (CreationalContext<?>) scopeMap.get(CREATIONAL + pc.getId());
                if (null != instance && null != creational) {
                    contextual.destroy(instance, creational);
                    creational.release();
                }
            }
        }
    }

    /**
     * Get the instance.
     *
     * @param <T> the type.
     * @param contextual the contextual.
     * @return the instance, or null.
     */
    public <T> T get(Contextual<T> contextual) {
        T result = null;

        String scopeId = (String) request.getAttribute(SCOPE_ID);
        if (null != scopeId) {
            HttpSession session = request.getSession();
            PassivationCapable pc = (PassivationCapable) contextual;
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(scopeId);
            if (null != scopeMap) {
                result = (T) scopeMap.get(INSTANCE + pc.getId());
            }
        }

        return result;
    }

    /**
     * Get the instance (create it if it does not exist).
     *
     * @param <T> the type.
     * @param contextual the contextual.
     * @param creational the creational.
     * @return the instance.
     */
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creational) {
        T result = get(contextual);

        if (result == null) {
            String scopeId = (String) request.getAttribute(SCOPE_ID);
            if (null == scopeId) {
                scopeId = generateScopeId();
            }
            HttpSession session = request.getSession();
            result = contextual.create(creational);
            PassivationCapable pc = (PassivationCapable) contextual;
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(scopeId);
            if (null != scopeMap) {
                session.setAttribute(scopeId, scopeMap);
                scopeMap.put(INSTANCE + pc.getId(), result);
                scopeMap.put(CREATIONAL + pc.getId(), creational);
            }
        }

        return result;
    }
    
    /**
     * Perform the work we need to do before a controller is called.
     * 
     * @param event the event.
     */
    public void beforeProcessControllerEvent(@Observes BeforeControllerEvent event) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("REDIRECT")) {
                    request.setAttribute(SCOPE_ID, cookie.getValue());
                }
            }
        }
    }

    /**
     * Perform the work we need to do at AfterProcessViewEvent time.
     *
     * @param event the event.
     */
    public void afterProcessViewEvent(@Observes AfterProcessViewEvent event) {
        if (request.getAttribute(SCOPE_ID) != null) {
            String scopeId = (String) request.getAttribute(SCOPE_ID);
            HttpSession session = request.getSession();
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(scopeId);
            if (null != scopeMap) {
                scopeMap.entrySet().stream().forEach((entrySet) -> {
                    String key = entrySet.getKey();
                    Object value = entrySet.getValue();
                    if (key.startsWith(INSTANCE)) {
                        Bean<?> bean = beanManager.resolve(beanManager.getBeans(value.getClass()));
                        destroy(bean);
                    }
                });
                scopeMap.clear();
                session.removeAttribute(scopeId);
            }
        }
    }

    /**
     * Perform the work we need to do at ControllerRedirectEvent time.
     *
     * @param event the event.
     */
    public void controllerRedirectEvent(@Observes ControllerRedirectEvent event) {
        if (request.getAttribute(SCOPE_ID) != null) {
            Cookie cookie = new Cookie("REDIRECT", request.getAttribute(SCOPE_ID).toString());
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * Generate the scope id.
     *
     * @return the scope id.
     */
    private String generateScopeId() {
        HttpSession session = request.getSession();
        String result = SCOPE_ID + "-" + UUID.randomUUID().toString();
        synchronized (this) {
            while (session.getAttribute(result) != null) {
                result = SCOPE_ID + "-" + UUID.randomUUID().toString();
            }
            session.setAttribute(result, new HashMap<>());
            request.setAttribute(SCOPE_ID, result);
        }
        return result;
    }
}
