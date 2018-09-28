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
package org.mvcspec.ozark.test.redirectscope2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;

/**
 * RedirectController test.
 *
 * @author Santiago Pericas-Geertsen
 */
@Path("redirect")
@Controller
@RequestScoped
public class RedirectController {

    @Inject
    RedirectBean redirectBean;

    @GET
    @Path("from")
    @Produces("text/html")
    public String getSource() {
        redirectBean.setValue("Redirect about to happen");
        return "redirect:/redirect/to";
    }

    @GET
    @Path("to")
    @Produces("text/html")
    public String getTarget() {
        return "redirect.jsp";
    }
}
