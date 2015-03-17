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
package com.oracle.ozark.test.produces;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ProducesIT {

    private final String ACCEPT_HEADER = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private final String ACCEPT_LANGUAGE = "en-US,en;q=0.8,es;q=0.6";

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
    public void testNoProduces1() throws Exception {
        final WebResponse wr = webClient.loadWebResponse(
                new WebRequest(new URL(webUrl + "resources/no_produces1"), ACCEPT_HEADER));
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.TEXT_HTML, wr.getContentType());
    }

    @Test
    public void testNoProduces2() throws Exception {
        final WebResponse wr = webClient.loadWebResponse(
                new WebRequest(new URL(webUrl + "resources/no_produces2"), ACCEPT_HEADER));
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.TEXT_HTML, wr.getContentType());
    }

    @Test
    public void testMultipleProduces1() throws Exception {
        final WebResponse wr = webClient.loadWebResponse(
                new WebRequest(new URL(webUrl + "resources/multiple_produces2"), ACCEPT_HEADER));
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
    }

    @Test
    public void testMultipleProduces2() throws Exception {
        final WebResponse wr = webClient.loadWebResponse(
                new WebRequest(new URL(webUrl + "resources/multiple_produces2"), ACCEPT_HEADER));
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
    }

    @Test
    public void otherProduces1() throws Exception {
        final WebResponse wr = webClient.loadWebResponse(
                new WebRequest(new URL(webUrl + "resources/other_produces1"), ACCEPT_HEADER));
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
    }

    @Test
    public void otherProduces2() throws Exception {
        final WebResponse wr = webClient.loadWebResponse(
                new WebRequest(new URL(webUrl + "resources/other_produces2"), ACCEPT_HEADER));
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
    }

    @Test
    public void language1() throws Exception {
        final WebRequest wrq = new WebRequest(new URL(webUrl + "resources/language1"), ACCEPT_HEADER);
        wrq.setAdditionalHeader("Accept-Language", ACCEPT_LANGUAGE);
        final WebResponse wr = webClient.loadWebResponse(wrq);
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
        assertEquals("es", wr.getResponseHeaderValue("Content-Language"));
    }

    @Test
    public void language2() throws Exception {
        final WebRequest wrq = new WebRequest(new URL(webUrl + "resources/language2"), ACCEPT_HEADER);
        wrq.setAdditionalHeader("Accept-Language", ACCEPT_LANGUAGE);
        final WebResponse wr = webClient.loadWebResponse(wrq);
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
        assertEquals("es", wr.getResponseHeaderValue("Content-Language"));
    }

    @Test
    public void locale1() throws Exception {
        final WebRequest wrq = new WebRequest(new URL(webUrl + "resources/locale1"), ACCEPT_HEADER);
        wrq.setAdditionalHeader("Accept-Language", ACCEPT_LANGUAGE);
        final WebResponse wr = webClient.loadWebResponse(wrq);
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
        assertEquals("en-GB", wr.getResponseHeaderValue("Content-Language"));
    }

    @Test
    public void locale2() throws Exception {
        final WebRequest wrq = new WebRequest(new URL(webUrl + "resources/locale2"), ACCEPT_HEADER);
        wrq.setAdditionalHeader("Accept-Language", ACCEPT_LANGUAGE);
        final WebResponse wr = webClient.loadWebResponse(wrq);
        assertEquals(Response.Status.OK.getStatusCode(), wr.getStatusCode());
        assertEquals(MediaType.APPLICATION_XHTML_XML, wr.getContentType());
        assertEquals("en-GB", wr.getResponseHeaderValue("Content-Language"));
    }
}
