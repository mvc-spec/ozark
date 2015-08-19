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

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The JUnit tests for the ModelsImpl class.
 * 
 * @author Manfred Riem (manfred.riem at oracle.com)
 */
@SuppressWarnings("unchecked")
public class ModelsImplTest {
    
    /**
     * Test size method.
     */
    @Test
    public void testSize() {
        ModelsImpl models = new ModelsImpl();
        assertEquals(0, models.size());
        models.put("1", "1");
        assertEquals(1, models.size());
    }
    
    /**
     * Test isEmpty method.
     */
    @Test
    public void testIsEmpty() {
        ModelsImpl models = new ModelsImpl();
        assertTrue(models.isEmpty());
        models.put("1", "1");
        assertFalse(models.isEmpty());
    }
    
    /**
     * Test containsKey method.
     */
    @Test
    public void testContainsKey() {
        ModelsImpl models = new ModelsImpl();
        assertFalse(models.containsKey("K"));
        models.put("K", "V");
        assertTrue(models.containsKey("K"));
    }
    
    /**
     * Test containsValue method.
     */
    @Test
    public void testContainsValue() {
        ModelsImpl models = new ModelsImpl();
        assertFalse(models.containsValue("V"));
        models.put("K", "V");
        assertTrue(models.containsValue("V"));
    }
    
    /**
     * Test get method.
     */
    @Test
    public void testGet() {
        ModelsImpl models = new ModelsImpl();
        assertNull(models.get("K"));
        models.put("K", "V");
        assertNotNull(models.get("K"));
        assertEquals("V", models.get("K"));
    }
    
    /**
     * Test remove method.
     */
    @Test
    public void testRemove() {
        ModelsImpl models = new ModelsImpl();
        assertEquals(0, models.size());
        models.put("K", "V");
        assertEquals(1, models.size());
        models.remove("K");
        assertEquals(0, models.size());
    }
    
    /**
     * Test putAll method.
     */
    @Test
    public void testPutAll() {
        ModelsImpl models = new ModelsImpl();
        assertEquals(0, models.size());
        HashMap otherMap = new HashMap();
        otherMap.put("1", "1V");
        otherMap.put("2", "2V");
        models.putAll(otherMap);
        assertEquals(2, models.size());
    }
    
    /**
     * Test clear method.
     */
    @Test
    public void testClear() {
        ModelsImpl models = new ModelsImpl();
        assertFalse(models.containsValue("V"));
        models.put("K", "V");
        assertEquals(1, models.size());
        models.clear();
        assertEquals(0, models.size());
    }
    
    /**
     * Test keySet method.
     */
    @Test
    public void testKeySet() {
        ModelsImpl models = new ModelsImpl();
        assertNotNull(models.keySet());
    }
    
    /**
     * Test values method.
     */
    @Test
    public void testValues() {
        ModelsImpl models = new ModelsImpl();
        assertNotNull(models.values());
    }
    
    /**
     * Test entrySet method.
     */
    @Test
    public void testEntrySet() {
        ModelsImpl models = new ModelsImpl();
        assertNotNull(models.entrySet());
    }
    
    /**
     * Test equals method.
     */
    @Test
    public void testEquals() {
        ModelsImpl modelsA = new ModelsImpl();
        ModelsImpl modelsB = new ModelsImpl();
        assertTrue(modelsA.equals(modelsB));
    }
    
    /**
     * Test hashCode method.
     */
    @Test
    public void testHashCode() {
        ModelsImpl modelsA = new ModelsImpl();
        ModelsImpl modelsB = new ModelsImpl();
        assertEquals(modelsA.hashCode(), modelsB.hashCode());
    }
    
    /**
     * Test iterator method.
     */
    @Test
    public void testIterator() {
        ModelsImpl models = new ModelsImpl();
        assertNotNull(models.iterator());
    }
}
