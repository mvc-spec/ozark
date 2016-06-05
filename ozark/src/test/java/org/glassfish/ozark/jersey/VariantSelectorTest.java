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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
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
