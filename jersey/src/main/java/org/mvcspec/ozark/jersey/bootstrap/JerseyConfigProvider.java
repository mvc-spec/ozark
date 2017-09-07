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
package org.mvcspec.ozark.jersey.bootstrap;

import org.mvcspec.ozark.bootstrap.ConfigProvider;
import org.mvcspec.ozark.jersey.binding.BindingInterceptorImpl;
import org.mvcspec.ozark.jersey.model.OzarkModelProcessor;

import javax.ws.rs.core.FeatureContext;

/**
 * Implementation of ConfigProvider for the Jersey module.
 *
 * @author Christian Kaltepoth
 */
public class JerseyConfigProvider implements ConfigProvider {

    @Override
    public void configure(FeatureContext context) {
        context.register(OzarkModelProcessor.class);
        context.register(BindingInterceptorImpl.class);
    }

}
