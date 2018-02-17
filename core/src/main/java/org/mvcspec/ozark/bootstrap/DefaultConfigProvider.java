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
package org.mvcspec.ozark.bootstrap;

import org.mvcspec.ozark.binding.convert.MvcConverterProvider;
import org.mvcspec.ozark.core.ViewRequestFilter;
import org.mvcspec.ozark.core.ViewResponseFilter;
import org.mvcspec.ozark.core.ViewableWriter;
import org.mvcspec.ozark.jaxrs.JaxRsContextFilter;
import org.mvcspec.ozark.locale.LocaleRequestFilter;
import org.mvcspec.ozark.security.CsrfExceptionMapper;
import org.mvcspec.ozark.security.CsrfProtectFilter;
import org.mvcspec.ozark.security.CsrfValidateInterceptor;

import javax.ws.rs.core.FeatureContext;

/**
 * Implementation of ConfigProvider which registers all providers of the core module.
 *
 * @author Christian Kaltepoth
 */
public class DefaultConfigProvider implements ConfigProvider {

    @Override
    public void configure(FeatureContext context) {

        context.register(ViewRequestFilter.class);
        context.register(ViewResponseFilter.class);
        context.register(ViewableWriter.class);
        context.register(CsrfValidateInterceptor.class);
        context.register(CsrfProtectFilter.class);
        context.register(CsrfExceptionMapper.class);
        context.register(LocaleRequestFilter.class);
        context.register(JaxRsContextFilter.class);
        context.register(MvcConverterProvider.class);

    }

}
