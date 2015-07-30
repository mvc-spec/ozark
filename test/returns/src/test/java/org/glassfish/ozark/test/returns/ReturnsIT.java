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
package org.glassfish.ozark.test.returns;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Tests all possible return types from a controller method, including an arbitrary
 * type on which {@code toString} is called and a {@code Response} that wraps
 * other types.
 *
 * @author Santiago Pericas-Geertsen
 */
public class ReturnsIT {

    private String webUrl;
    private WebClient webClient;

    @Before
    public void setUp() {
        webUrl = System.getProperty("integration.url");
        webClient = new WebClient();
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    @Test
    public void testString() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/string");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }

    @Test
    public void testViewable() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/viewable");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }

    @Test
    public void testResponse() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/response");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }

    @Test
    public void testMyViewable() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/myviewable");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }

    @Test
    public void testMyViewableNull() throws Exception {
        try {
            final HtmlPage page = webClient.getPage(webUrl + "resources/myviewable/null");
        } catch (FailingHttpStatusCodeException e) {
            assertEquals(e.getStatusCode(), 500);
            return;
        }
        fail();
    }

    @Test
    public void testResponseViewable() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/response/viewable");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }

    @Test
    public void testResponseMyViewable() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/response/myviewable");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }
}
