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
package org.mvcspec.ozark.core;

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
