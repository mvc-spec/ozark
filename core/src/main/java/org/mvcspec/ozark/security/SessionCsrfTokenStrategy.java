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
package org.mvcspec.ozark.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Session-based implementation if {@link CsrfTokenStrategy}.
 *
 * @author Christian Kaltepoth
 */
public class SessionCsrfTokenStrategy implements CsrfTokenStrategy {

    private static final String SESSION_KEY = SessionCsrfTokenStrategy.class.getName() + ".TOKEN";

    @Override
    public Optional<CsrfToken> getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object value = session.getAttribute(SESSION_KEY);
            if (value instanceof CsrfToken) {
                return Optional.of((CsrfToken) value);
            }
        }
        return Optional.empty();
    }

    @Override
    public void storeToken(HttpServletRequest request, CsrfToken token) {
        request.getSession(true).setAttribute(SESSION_KEY, token);
    }
}
