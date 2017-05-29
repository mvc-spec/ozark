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
package org.glassfish.ozark.test.events;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertTrue;

public class EventsIT {

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
    public void testEvents() throws Exception {
        final HtmlPage page = webClient.getPage(webUrl + "resources/event");
        final Iterator<HtmlElement> it = page.getDocumentElement().getHtmlElementsByTagName("p").iterator();
        assertTrue(it.next().asText().contains("test-events/resources/event"));
        assertTrue(it.next().asText().contains("EventController.get()"));
        assertTrue(it.next().asText().contains("test-events/resources/event"));
        assertTrue(it.next().asText().contains("EventController.get()"));
        assertTrue(it.next().asText().contains("event.jsp"));
        assertTrue(it.next().asText().contains("JspViewEngine"));
    }
}
