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
package org.mvcspec.ozark.test.jade;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.Controller;
import javax.mvc.View;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author Florian Hirsch
 */
@Path("/")
@Controller
public class JadeController {

    @Inject
    private Models models;

    @GET
    @Controller
    @View("main.jade")
    public void get(@QueryParam("user") String user) {
        models.put("user", user);
        models.put("pageName", "Hello Jade");
    }

    @GET
    @Path("/markdown")
    @Controller
    @View("markdown.jade")
    public void getMarkdownd(@QueryParam("article") String article) {
        models.put("pageName", "Markdown Introduction");
    }

    @GET
    @Path("/config")
    @Controller
    @View("config.jade")
    public void getCofig(@QueryParam("article") String article) {
        models.put("pageName", "Configuration");
    }

    @GET
    @Path("/helper")
    @Controller
    @View("helper.jade")
    public void getCofig() {
        models.put("pageName", "Helper");
    }
}
