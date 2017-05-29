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
package org.glassfish.ozark.jersey;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;
import org.junit.Test;

import javax.mvc.annotation.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertTrue;

/**
 * The JUnit test for the OzarkModelProcessor class.
 *
 * @author Florian Hirsch
 */
public class OzarkModelProcessorTest {

	@Test
	public void processResourceModel() {
		ResourceModel.Builder rmb = new ResourceModel.Builder(false);
		Resource resource = Resource.builder(SomeController.class).build();
		rmb.addResource(resource);
		ResourceModel processedModel = new OzarkModelProcessor().processResourceModel(rmb.build(), null);
		Resource processedResource = processedModel.getResources().get(0);
		processedResource.getResourceMethods().forEach(m -> assertTrue(m.getProducedTypes().contains(MediaType.TEXT_HTML_TYPE)));
	}

	@Controller
	private static class SomeController {
		@GET public void view1() {}
		@GET @Produces(MediaType.TEXT_HTML) public void view2() {}
	}
}
