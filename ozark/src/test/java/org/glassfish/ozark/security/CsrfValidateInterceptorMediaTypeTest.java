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

import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class CsrfValidateInterceptorMediaTypeTest {

  @Test
  public void shouldSupportFormMediaType() {
    assertEquals(true, CsrfValidateInterceptor.isSupportedMediaType(
        MediaType.valueOf("application/x-www-form-urlencoded")
    ));
  }

  @Test
  public void shouldSupportFormMediaTypeWithCharset() {
    // https://java.net/jira/browse/OZARK-66
    assertEquals(true, CsrfValidateInterceptor.isSupportedMediaType(
        MediaType.valueOf("application/x-www-form-urlencoded;charset=windows-31j")
    ));
  }

  @Test
  public void shouldFailForOtherMediaType() {
    assertEquals(false, CsrfValidateInterceptor.isSupportedMediaType(
        MediaType.valueOf("application/pdf")
    ));
  }

  @Test
  public void shouldFailForNoMediaType() {
    assertEquals(false, CsrfValidateInterceptor.isSupportedMediaType(null));
  }

}