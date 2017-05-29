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
package org.glassfish.ozark.jersey;

import org.easymock.EasyMock;
import org.glassfish.jersey.message.internal.AcceptableLanguageTag;
import org.glassfish.jersey.message.internal.AcceptableMediaType;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * The JUnit test for the ViewEngineBase class.
 *
 * @author Florian Hirsch
 */
public class VariantSelectorTest {

	@Test
	public void getQualifiedAcceptableMediaTypes() {
		HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
		expect(request.getHeader(HttpHeaders.ACCEPT)).andReturn(null);
		expect(request.getHeader(HttpHeaders.ACCEPT)).andReturn("text/plain");
		expect(request.getHeader(HttpHeaders.ACCEPT)).andReturn("text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, */*;q=0.8");
		replay(request);
		assertThat(VariantSelector.getQualifiedAcceptableMediaTypes(request), is(Collections.singletonList(
				new AcceptableMediaType("*", "*")
		)));
		assertThat(VariantSelector.getQualifiedAcceptableMediaTypes(request), is(Collections.singletonList(
				new AcceptableMediaType("text", "plain")
		)));
		assertThat(VariantSelector.getQualifiedAcceptableMediaTypes(request), is(Arrays.asList(
				new AcceptableMediaType("text", "html"),
				new AcceptableMediaType("application", "xhtml+xml"),
				new AcceptableMediaType("image", "webp"),
				new AcceptableMediaType("application", "xml", 900, Collections.emptyMap()),
				new AcceptableMediaType("*", "*", 800, Collections.emptyMap())
		)));
		verify(request);
	}

	@Test
	public void getQualifiedAcceptableLanguages() throws ParseException {
		HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
		expect(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)).andReturn(null);
		expect(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)).andReturn("en-US");
		expect(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)).andReturn("de-DE, de;q=0.8, en-US;q=0.6, en;q=0.4");
		replay(request);
		assertEquals(VariantSelector.getQualifiedAcceptableLanguages(request), Collections.singletonList(new AcceptableLanguageTag("*", null)));
		assertEquals(VariantSelector.getQualifiedAcceptableLanguages(request), Collections.singletonList(new AcceptableLanguageTag("en", "US")));
		assertEquals(VariantSelector.getQualifiedAcceptableLanguages(request), Arrays.asList(
				new AcceptableLanguageTag("de", "DE"),
				new AcceptableLanguageTag("de;q=0.8"),
				new AcceptableLanguageTag("en-US;q=0.6"),
				new AcceptableLanguageTag("en;q=0.4")
		));
		verify(request);
	}

	// org.glassfish.jersey.message.internal.LanguageTag#equals is broken (JERSEY-3069)
	public void assertEquals(List<AcceptableLanguageTag> expected, List<AcceptableLanguageTag> actual) {
		for (int i = 0; i < expected.size(); i++) {
			assertThat(expected.get(i).getPrimaryTag(), is(actual.get(i).getPrimaryTag()));
			assertThat(expected.get(i).getSubTags(), is(actual.get(i).getSubTags()));
			assertThat(expected.get(i).getQuality(), is(actual.get(i).getQuality()));
		}
	}

	@Test
	public void selectVariant() throws Exception {
		ResourceInfo resourceInfo = EasyMock.createStrictMock(ResourceInfo.class);
		expect(resourceInfo.getResourceMethod()).andReturn(SomeController.class.getMethod("method1"));
		expect(resourceInfo.getResourceMethod()).andReturn(SomeController.class.getMethod("method2"));
		expect(resourceInfo.getResourceMethod()).andReturn(SomeController.class.getMethod("method3"));
		HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
		expect(request.getHeader(HttpHeaders.ACCEPT)).andReturn("text/html");
		expect(request.getHeader(HttpHeaders.ACCEPT)).andReturn("text/html, application/xml");
		expect(request.getHeader(HttpHeaders.ACCEPT)).andReturn("text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, */*;q=0.8");
		replay(resourceInfo, request);
		assertThat(VariantSelector.selectVariant(request, resourceInfo), is(MediaType.TEXT_HTML_TYPE));
		assertThat(VariantSelector.selectVariant(request, resourceInfo), is(MediaType.APPLICATION_XML_TYPE));
		assertThat(VariantSelector.selectVariant(request, resourceInfo), is(MediaType.APPLICATION_XHTML_XML_TYPE));
		verify(resourceInfo, request);
	}

	private static class SomeController {
		@Produces("text/html") public void method1() {}
		@Produces({"application/json", "application/xml"}) public void method2() {}
		@Produces({"application/xhtml+xml", "application/xml"}) public void method3() {}
	}

}
