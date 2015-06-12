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
package com.oracle.ozark.test.exceptions;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests responses that return error codes like 400 and 500 to ensure that views
 * are processed correctly.
 *
 * @author Santiago Pericas-Geertsen
 */
public class ExceptionsIT {

    private String webUrl;
    private WebClient webClient;

    @Before
    public void setUp() {
        webUrl = System.getProperty("integration.url");
        webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    @Test
    @Ignore
    public void testNotFound() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/exceptions/not_found");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }

    @Test
    public void testNotFoundNoView() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/exceptions/not_found_no_view");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertFalse(it.next().asText().contains("Hello World"));
    }

    @Test
    @Ignore
    public void testInternalError() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/exceptions/internal_error");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }

    @Test
    public void testInternalErrorNoView() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/exceptions/internal_error_no_view");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertFalse(it.next().asText().contains("Hello World"));
    }

    @Test
    @Ignore
    public void testInternalErrorMapped() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/exceptions/internal_error_mapped");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("Hello World"));
    }
}
