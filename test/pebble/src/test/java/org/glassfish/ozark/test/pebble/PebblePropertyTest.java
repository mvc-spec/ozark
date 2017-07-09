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

import java.util.stream.Stream;
import org.glassfish.ozark.ext.pebble.PebbleProperty;
import static org.junit.Assert.*;
import org.junit.Test;

public class PebblePropertyTest {

  @Test
  public void propertyKeyShouldStartWithGroupPrefix() {
    final String groupPrefix = "org.glassfish.ozark.ext.pebble.";

    Stream.of(PebbleProperty.values()).forEach(property -> {
      assertTrue(property.key().startsWith(groupPrefix));
    });
  }

  @Test
  public void shouldResolvePebblePropertyFromStringKey() {
    Stream.of(PebbleProperty.values()).forEach(property -> {
      assertEquals(property, PebbleProperty.fromKey(property.key()));
    });
  }

  @Test
  public void shouldReturnSystemPropertyValueAsOptional() {
    System.setProperty(PebbleProperty.STRICT_VARIABLES.key(), "false");

    assertEquals("false", PebbleProperty.STRICT_VARIABLES.systemPropertyValue().get());
    assertFalse(PebbleProperty.NEW_LINE_TRIMMING.systemPropertyValue().isPresent());
  }
}
