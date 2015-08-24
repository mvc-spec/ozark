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

import org.glassfish.ozark.Properties;
import org.glassfish.ozark.event.ControllerRedirectEventImpl;
import org.glassfish.ozark.util.PropertyUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;
import javax.mvc.MvcContext;
import javax.mvc.event.AfterProcessViewEvent;
import javax.mvc.event.BeforeControllerEvent;
import javax.mvc.event.ControllerRedirectEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The ApplicationScoped redirect scope manager.
 *
 * @author Manfred Riem (manfred.riem at oracle.com)
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
@SuppressWarnings("unchecked")
public class RedirectScopeManager {

    private static final String PREFIX = "org.glassfish.ozark.redirect.";
    private static final String SCOPE_ID = PREFIX + "ScopeId";
    private static final String INSTANCE = "Instance-";
    private static final String CREATIONAL = "Creational-";
    private static final String COOKIE_NAME = PREFIX + "Cookie";

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
     * Application's configuration.
     */
    @Context
    private Configuration config;
    
    /**
     * Stores the MVC context.
     */
    @Inject
    private MvcContext mvc;

    /**
     * Destroy the instance.
     *
     * @param contextual the contextual.
     */
    public void destroy(Contextual contextual) {
        String scopeId = (String) request.getAttribute(SCOPE_ID);
        if (null != scopeId) {
            HttpSession session = request.getSession();
            if (contextual instanceof PassivationCapable == false) {
                throw new RuntimeException("Unexpected type for contextual");
            }
            PassivationCapable pc = (PassivationCapable) contextual;
            final String sessionKey = SCOPE_ID + "-" + scopeId;
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(sessionKey);
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
            if (contextual instanceof PassivationCapable == false) {
                throw new RuntimeException("Unexpected type for contextual");
            }
            PassivationCapable pc = (PassivationCapable) contextual;
            final String sessionKey = SCOPE_ID + "-" + scopeId;
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(sessionKey);
            if (null != scopeMap) {
                result = (T) scopeMap.get(INSTANCE + pc.getId());
            } else {
                request.setAttribute(SCOPE_ID, null);       // old cookie, force new scope generation
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
            if (contextual instanceof PassivationCapable == false) {
                throw new RuntimeException("Unexpected type for contextual");
            }
            PassivationCapable pc = (PassivationCapable) contextual;
            final String sessionKey = SCOPE_ID + "-" + scopeId;
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(sessionKey);
            if (null != scopeMap) {
                session.setAttribute(sessionKey, scopeMap);
                scopeMap.put(INSTANCE + pc.getId(), result);
                scopeMap.put(CREATIONAL + pc.getId(), creational);
            }
        }

        return result;
    }
    
    /**
     * Update SCOPE_ID request attribute based on either cookie or URL query param
     * information received in the request.
     * 
     * @param event the event.
     */
    public void beforeProcessControllerEvent(@Observes BeforeControllerEvent event) {
        if (usingCookies()) {
            final Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(COOKIE_NAME)) {
                        request.setAttribute(SCOPE_ID, cookie.getValue());
                        return;     // we're done
                    }
                }
            }
        } else {
            final String scopeId = event.getUriInfo().getQueryParameters().getFirst(SCOPE_ID);
            if (scopeId != null) {
                request.setAttribute(SCOPE_ID, scopeId);
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
            final String sessionKey = SCOPE_ID + "-" + scopeId;
            Map<String, Object> scopeMap = (Map<String, Object>) session.getAttribute(sessionKey);
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
                session.removeAttribute(sessionKey);
            }
        }
    }

    /**
     * Upon detecting a redirect, either add cookie to response or re-write URL of new
     * location to co-relate next request.
     *
     * @param event the event.
     */
    public void controllerRedirectEvent(@Observes ControllerRedirectEvent event) {
        if (request.getAttribute(SCOPE_ID) != null) {
            if (usingCookies()) {
                Cookie cookie = new Cookie(COOKIE_NAME, request.getAttribute(SCOPE_ID).toString());
                cookie.setPath(mvc.getContextPath());
                cookie.setMaxAge(600);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            } else {
                final ContainerResponseContext crc = ((ControllerRedirectEventImpl) event).getContainerResponseContext();
                final UriBuilder builder = UriBuilder.fromUri(crc.getStringHeaders().getFirst(HttpHeaders.LOCATION));
                builder.queryParam(SCOPE_ID, request.getAttribute(SCOPE_ID).toString());
                crc.getHeaders().putSingle(HttpHeaders.LOCATION, builder.build());
            }
        }
    }

    /**
     * Generate the scope id.
     *
     * @return the scope id.
     */
    private String generateScopeId() {
        HttpSession session = request.getSession();
        String scopeId = UUID.randomUUID().toString();
        String sessionKey = SCOPE_ID + "-" + scopeId;
        synchronized (this) {
            while (session.getAttribute(sessionKey) != null) {
                scopeId = UUID.randomUUID().toString();
                sessionKey = SCOPE_ID + "-" + scopeId;
            }
            session.setAttribute(sessionKey, new HashMap<>());
            request.setAttribute(SCOPE_ID, scopeId);
        }
        return scopeId;
    }

    /**
     * Checks application configuration to see if cookies should be used.
     *
     * @return value of property.
     */
    private boolean usingCookies() {
        return PropertyUtils.getProperty(config, Properties.REDIRECT_SCOPE_COOKIES, false);
    }
}
