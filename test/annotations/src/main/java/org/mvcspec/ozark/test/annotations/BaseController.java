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
package org.mvcspec.ozark.test.annotations;

import javax.mvc.Controller;
import javax.mvc.View;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Class BaseController.
 *
 * @author Santiago Pericas-Geertsen
 */
public class BaseController {

    @GET
    @Controller
    @Path("no_view")
    public String method() {
        return "error.jsp";
    }

    @GET
    @Controller
    @Path("view")
    @View("success.jsp")
    public void methodWithView() {
    }

    @GET
    @Controller
    @Path("no_override_jaxrs")
    @View("error.jsp")
    public void methodNoOverrideJaxrs() {
    }

    @GET
    @Controller
    @Path("no_override_mvc")
    @View("error.jsp")
    public void methodNoOverrideMvc() {
    }
}
