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
package org.glassfish.ozark.test.pebble;

import java.util.Properties;
import org.glassfish.ozark.ext.pebble.PebbleConfigurationProducer;
import org.glassfish.ozark.ext.pebble.PebbleProperty;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PebbleConfigurationProducerTest {

  PebbleConfigurationProducer pebbleConfigurationProducer;

  @Before
  public void setup() {
    pebbleConfigurationProducer = new PebbleConfigurationProducer();
    System.clearProperty(PebbleProperty.AUTO_ESCAPING.key());
  }

  @Test
  public void shouldReturnEmptyPropertiesForNonExistingPropertiesFile() {
    Properties pebbleConfiguration = pebbleConfigurationProducer.loadFromFile("unexisting.properties");

    assertNotNull(pebbleConfiguration);
    assertTrue(pebbleConfiguration.isEmpty());
  }

  @Test
  public void shouldLoadPebbleConfigurationPropertiesFromFile() {
    Properties pebbleConfiguration = pebbleConfigurationProducer.loadFromFile("pebble.properties");

    assertNotNull(pebbleConfiguration);
    assertTrue(pebbleConfiguration.size() == 1);
    assertEquals(pebbleConfiguration.getProperty("org.glassfish.ozark.ext.pebble.autoEscaping"), "true");
  }

  @Test
  public void shouldReturnPebbleConfiguration() {
    Properties pebbleConfiguration = pebbleConfigurationProducer.pebbleConfiguration();

    assertNotNull(pebbleConfiguration);
    assertTrue(pebbleConfiguration.size() == 1);
    assertEquals(pebbleConfiguration.getProperty("org.glassfish.ozark.ext.pebble.autoEscaping"), "true");
  }

  @Test
  public void systemPropertyShouldHaveHigherPriorityThanTheOneFromPropertyFile() {
    System.setProperty(PebbleProperty.AUTO_ESCAPING.key(), "false-override");

    Properties pebbleConfiguration = pebbleConfigurationProducer.pebbleConfiguration();

    assertNotNull(pebbleConfiguration);
    assertTrue(pebbleConfiguration.size() == 1);
    assertEquals(pebbleConfiguration.getProperty(PebbleProperty.AUTO_ESCAPING.key()), "false-override");
  }
}
