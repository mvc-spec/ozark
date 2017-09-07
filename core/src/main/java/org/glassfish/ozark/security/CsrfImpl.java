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
package org.glassfish.ozark.security;

import javax.enterprise.context.SessionScoped;
import javax.mvc.security.Csrf;
import java.io.Serializable;
import java.util.UUID;

/**
 * CSRF bean in session scope available for injection and in EL via the {@link
 * javax.mvc.MvcContext} object as {@code mvc.csrf}. Provides access to the CSRF
 * header name (a constant) and the CSRF token value (one per session).
 *
 * @author Santiago Pericas-Geertsen
 */
@SessionScoped
public class CsrfImpl implements Csrf, Serializable {

    private static final String CSRF_HEADER = "X-Requested-By";

    private static final long serialVersionUID = -403250971215462525L;

    private UUID token = UUID.randomUUID();

    public String getName() {
        return CSRF_HEADER;
    }

    public String getToken() {
        return token.toString();
    }
}
