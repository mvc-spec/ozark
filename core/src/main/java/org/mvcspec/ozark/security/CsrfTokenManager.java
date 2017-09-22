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

import org.mvcspec.ozark.OzarkConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central class for managing CSRF tokens
 *
 * @author Christian Kaltepoth
 */
@ApplicationScoped
public class CsrfTokenManager {

    private static final Logger log = Logger.getLogger(CsrfTokenManager.class.getName());

    private CsrfTokenStrategy tokenStrategy;

    @Inject
    private Instance<HttpServletRequest> requestInstance;

    @Inject
    private OzarkConfig ozarkConfig;

    @PostConstruct
    public void init() {

        this.tokenStrategy = ozarkConfig.getCsrfTokenStrategy();

        log.log(Level.FINE, "Configured strategy for CSRF tokens: {0}", new Object[]{
                this.tokenStrategy.getClass().getName()
        });

    }

    public Optional<CsrfToken> getToken() {
        return tokenStrategy.getToken(requestInstance.get());
    }

    public CsrfToken getOrCreateToken() {
        return getToken().orElseGet(() -> createAndStoreToken());
    }

    private CsrfToken createAndStoreToken() {
        CsrfToken csrfToken = CsrfToken.generate();
        tokenStrategy.storeToken(requestInstance.get(), csrfToken);
        return csrfToken;
    }

}
