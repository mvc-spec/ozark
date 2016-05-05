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
package org.glassfish.ozark.core;

import org.easymock.EasyMock;
import org.glassfish.ozark.engine.ViewEngineFinder;
import org.junit.Test;

import javax.enterprise.event.Event;
import javax.mvc.event.MvcEvent;
import javax.ws.rs.core.Configuration;
import javax.mvc.Viewable;
import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The JUnit tests for the ViewableWriter class.
 * 
 * @author Manfred Riem (manfred.riem at oracle.com)
 */
@SuppressWarnings("unchecked")
public class ViewableWriterTest {
    
    /**
     * Test isWriteable method.
     */
    @Test
    public void testIsWriteable() {
        ViewableWriter writer = new ViewableWriter();
        assertFalse(writer.isWriteable(null, null, new Annotation[] {}, MediaType.WILDCARD_TYPE));
        assertTrue(writer.isWriteable(Viewable.class, null, new Annotation[] {}, MediaType.WILDCARD_TYPE));
    }
    
    /**
     * Test getSize method.
     */
    @Test
    public void testGetSize() {
        ViewableWriter writer = new ViewableWriter();
        assertEquals(-1, writer.getSize(null, null, null, new Annotation[] {}, MediaType.WILDCARD_TYPE));
    }
    
    /**
     * Test writeTo method.
     * 
     * @throws Exception when a serious error occurs.
     */
    @Test
    public void testWriteTo() throws Exception {
        ViewableWriter writer = new ViewableWriter();
        
        ViewEngineFinder finder = EasyMock.createStrictMock(ViewEngineFinder.class);
        Field finderField = writer.getClass().getDeclaredField("engineFinder");
        finderField.setAccessible(true);
        finderField.set(writer, finder);
        
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        Field requestField = writer.getClass().getDeclaredField("request");
        requestField.setAccessible(true);
        requestField.set(writer, request);

        Event<MvcEvent> dispatcher = EasyMock.createStrictMock(Event.class);
        Field dispatcherField = writer.getClass().getDeclaredField("dispatcher");
        dispatcherField.setAccessible(true);
        dispatcherField.set(writer, dispatcher);
        
        ViewEngine viewEngine = EasyMock.createStrictMock(ViewEngine.class);
        
        HttpServletResponse response = EasyMock.createStrictMock(HttpServletResponse.class);
        Field responseField = writer.getClass().getDeclaredField("response");
        responseField.setAccessible(true);
        responseField.set(writer, response);

        Configuration config = EasyMock.createStrictMock(Configuration.class);
        Field configField = writer.getClass().getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(writer, config);

        MultivaluedHashMap map = new MultivaluedHashMap();
        ArrayList<MediaType> contentTypes = new ArrayList<>();
        contentTypes.add(MediaType.TEXT_HTML_TYPE);
        map.put("Content-Type", contentTypes);
        
        Viewable viewable = new Viewable("myview");
        viewable.setModels(new ModelsImpl());

        expect(finder.find(anyObject())).andReturn(viewEngine);
        viewEngine.processView((ViewEngineContext) anyObject());

        replay(finder, request, viewEngine, response);
        writer.writeTo(viewable, null, null, new Annotation[] {}, MediaType.WILDCARD_TYPE, map, null);
        verify(finder, request, viewEngine, response);
    }
}
