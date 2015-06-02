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
package com.oracle.ozark.test.csrfproperty;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.Iterator;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests CSRF implementation. Ensures hidden field is returned and that a form submitted
 * without it results in a 403 error.
 *
 * @author Santiago Pericas-Geertsen
 */
public class CsrfIT {

    private static final String CSRF_HEADER = "X-Requested-By";

    private String webUrl;
    private WebClient webClient;

    @Before
    public void setUp() {
        webUrl = System.getProperty("integration.url");
        webClient = new WebClient();
        webClient.getOptions().setRedirectEnabled(true);        // enable redirect!
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    /**
     * Retrieve a form and submit it making sure the CSRF hidden field is present
     *
     * @throws Exception an error occurs or validation fails.
     */
    @Test
    public void testFormOk() throws Exception {
        HtmlPage page1 = webClient.getPage(webUrl + "resources/csrf");
        HtmlForm form = (HtmlForm) page1.getDocumentElement().getHtmlElementsByTagName("form").get(0);

        // Check hidden input field
        HtmlElement input = form.getHtmlElementsByTagName("input").get(1);
        assertTrue(input.getAttribute("type").equals("hidden"));
        assertTrue(input.getAttribute("name").equals(CsrfProtectionFilter.HEADER_NAME));
        assertTrue(input.hasAttribute("value"));        // token

        // Submit form
        HtmlSubmitInput button = (HtmlSubmitInput) form.getHtmlElementsByTagName("input").get(0);
        HtmlPage page2 = button.click();
        Iterator<HtmlElement> it = page2.getDocumentElement().getHtmlElementsByTagName("h1").iterator();
        assertTrue(it.next().asText().contains("CSRF Protection OK"));
    }

    /**
     * Retrieves a form, removes CSRF hidden field and attempts to submit. Should
     * result in a 403 error.
     *
     * @throws Exception an error occurs or validation fails.
     */
    @Test
    public void testFormFail() throws Exception {
        HtmlPage page1 = webClient.getPage(webUrl + "resources/csrf");
        HtmlForm form = (HtmlForm) page1.getDocumentElement().getHtmlElementsByTagName("form").get(0);

        // Remove hidden input field to cause a CSRF validation failure
        HtmlElement input = form.getHtmlElementsByTagName("input").get(1);
        form.removeChild(input);

        // Submit form - should fail
        HtmlSubmitInput button = (HtmlSubmitInput) form.getHtmlElementsByTagName("input").get(0);
        try {
            button.click();
            fail("CSRF validation should have failed!");
        } catch (FailingHttpStatusCodeException e) {
            // falls through
        }
    }

    /**
     * Checks that CSRF validation works if token sent as header instead of
     * form field.
     *
     * @throws Exception an error occurs or validation fails.
     */
    @Test
    public void testFormHeaderOk() throws Exception {
        HtmlPage page1 = webClient.getPage(webUrl + "resources/csrf");

        // Check response and CSRF header
        WebResponse res = page1.getWebResponse();
        assertEquals(Response.Status.OK.getStatusCode(), res.getStatusCode());
        assertNotNull(res.getResponseHeaderValue(CSRF_HEADER));

        WebRequest req = new WebRequest(new URL(webUrl + "resources/csrf"));
        req.setHttpMethod(HttpMethod.POST);
        req.setAdditionalHeader(CSRF_HEADER, res.getResponseHeaderValue(CSRF_HEADER));
        res = webClient.loadWebResponse(req);
        assertEquals(Response.Status.OK.getStatusCode(), res.getStatusCode());
    }
}

