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
package org.glassfish.ozark.bootstrap;

import javax.ws.rs.core.FeatureContext;

/**
 * SPI used to register providers when Ozark is initialized. Implementations are discovered
 * using the JDK's ServiceLoader mechanism.
 *
 * @author Christian Kaltepoth
 */
public interface ConfigProvider {

    void configure(FeatureContext context);

}
