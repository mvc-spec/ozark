/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.ozark.core;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class Messages.
 *
 * @author Santiago Pericas-Geertsen
 */
@ApplicationScoped
public class Messages {

    private static final String BASENAME = "ozark";

    @Inject
    private HttpServletRequest request;

    /**
     * Get a message given its key and using the locales in the current request 'Accept-Language'
     * header. If no bundle or key found, try using the {@link java.util.Locale#ENGLISH} locale in
     * an attempt to produce some message.
     *
     * @param key The key to search.
     * @param params Message parameters.
     * @return Message or {@code null} if bundle or key not found for any locale.
     */
    public String get(String key, Object... params) {
        ResourceBundle rb;
        if (request != null) {
            final Enumeration<Locale> locales = request.getLocales();
            while (locales.hasMoreElements()) {
                final String message = get(key, locales.nextElement(), params);
                if (message != null) {
                    return message;
                }
            }
        }
        // English message better than no message
        return get(key, Locale.ENGLISH, params);
    }

    /**
     * Get a message given its key and a locale.
     *
     * @param key The key to search.
     * @param params Message parameters.
     * @return Formatted message or {@code null} if bundle or key not found.
     */
    public String get(String key, Locale locale, Object... params) {
        try {
            final ResourceBundle rb = ResourceBundle.getBundle(BASENAME, locale);
            final String pattern = rb.getString(key);
            return MessageFormat.format(pattern, params);
        } catch (MissingResourceException e) {
            return null;
        }
    }
}
