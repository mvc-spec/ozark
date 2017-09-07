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
package org.mvcspec.ozark.validation;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Triggers validation of parameters in {@link javax.mvc.annotation.Controller} methods.
 * This annotation is added by the CDI extension to all controller methods.
 *
 * @author Dmytro Maidaniuk
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({METHOD, TYPE})
public @interface ValidationInterceptorBinding {

}
