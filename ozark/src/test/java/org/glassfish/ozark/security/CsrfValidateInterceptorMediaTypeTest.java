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