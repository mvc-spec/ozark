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
import org.mvcspec.ozark.util.CdiUtils;

import javax.ws.rs.core.FeatureContext;
import java.util.List;

/**
 * Implementation of ConfigProvider which registers all providers of the core module.
 *
 * @author Christian Kaltepoth
 */
public class DefaultConfigProvider implements ConfigProvider {

    @Override
    public void configure(FeatureContext context) {

        register(context, ViewRequestFilter.class);
        register(context, ViewResponseFilter.class);
        register(context, ViewableWriter.class);
        register(context, CsrfValidateInterceptor.class);
        register(context, CsrfProtectFilter.class);
        register(context, CsrfExceptionMapper.class);
        register(context, LocaleRequestFilter.class);
        register(context, JaxRsContextFilter.class);
        register(context, MvcConverterProvider.class);

    }

    private void register(FeatureContext context, Class<?> providerClass) {

        boolean isCxf = context.getClass().getName().startsWith("org.apache.cxf");

        /*
         * With CXF there is no CDI injection if JAX-RS providers are registered via
         * context.register(Class). So we try to lookup provider instances from CDI
         * and register them instead.
         * See: https://issues.apache.org/jira/browse/CXF-7501
         */
        if (isCxf) {
            List<?> providerInstances = CdiUtils.getApplicationBeans(providerClass);
            if (!providerInstances.isEmpty()) {
                context.register(providerInstances.get(0));
            } else {
                context.register(providerClass);
            }
        }

        // will work for all other containers
        else {
            context.register(providerClass);
        }


    }

}
