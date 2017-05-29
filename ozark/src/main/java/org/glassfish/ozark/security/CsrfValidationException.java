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

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

/**
 * Custom exception thrown if CSRF token validation failed. Will ensure a corresponding
 * response phrase is send to the client.
 *
 * @author Christian Kaltepoth
 */
public class CsrfValidationException extends ForbiddenException {

    private static final long serialVersionUID = 303250971213462525L;

    public CsrfValidationException(String message) {
        super(message, Response.status(new CsrfValidationStatusType(message)).build());
    }

    /**
     * Custom implementation of {@link javax.ws.rs.core.Response.StatusType} that allows
     * to customize the response phrase.
     */
    private static class CsrfValidationStatusType implements Response.StatusType {

        private final Response.StatusType status = Response.Status.FORBIDDEN;

        private final String phrase;

        public CsrfValidationStatusType(String phrase) {
            this.phrase = phrase;
        }

        @Override
        public int getStatusCode() {
            return status.getStatusCode();
        }

        @Override
        public Response.Status.Family getFamily() {
            return status.getFamily();
        }

        @Override
        public String getReasonPhrase() {
            return phrase;
        }

    }

}
