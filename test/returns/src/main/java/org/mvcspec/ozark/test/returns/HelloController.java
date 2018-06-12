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
package org.mvcspec.ozark.test.returns;

import org.mvcspec.ozark.engine.Viewable;

import javax.mvc.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Tests all possible return types from a controller method, including an arbitrary
 * type on which {@code toString} is called and a {@code Response} that wraps
 * other types.
 *
 * @author Santiago Pericas-Geertsen
 */
@Path("/")
@Controller
public class HelloController {

    @GET
    @Path("string")
    public String getString() {
        return "hello.jsp";
    }

    @GET
    @Path("viewable")
    public Viewable getViewable() {
        return new Viewable("hello.jsp");
    }

    @GET
    @Path("response")
    public Response getResponse() {
        return Response.ok().entity("hello.jsp").build();
    }

    private static class MyViewable {
        private String view;

        public MyViewable(String view) {
            this.view = view;
        }

        @Override
        public String toString() {
            return view;
        }
    }

    @GET
    @Path("myviewable")
    public MyViewable getMyViewable() {
        return new MyViewable("hello.jsp");
    }

    @GET
    @Path("myviewable/null")
    public MyViewable getMyViewableNull() {
        return new MyViewable(null);        // Error condition
    }

    @GET
    @Path("response/viewable")
    public Response getResponseViewable() {
        return Response.ok().entity(new Viewable("hello.jsp")).build();
    }

    @GET
    @Path("response/myviewable")
    public Response getResponseMyViewable() {
        return Response.ok().entity(new MyViewable("hello.jsp")).build();
    }
}
