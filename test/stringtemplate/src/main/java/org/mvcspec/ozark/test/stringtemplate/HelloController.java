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
package org.mvcspec.ozark.test.stringtemplate;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.annotation.*;
import javax.ws.rs.*;

/**
 * HelloController test.
 *
 * @author Rodrigo Turini
 */
@Path("hello")
public class HelloController {

    @Inject
    private Models models;

    @GET
    @Controller
    @Produces("text/html")
    @View("hello.st")
    public void hello(@QueryParam("user") String user) {
        models.put("user", user);
    }
}
