/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.ozark.jersey;

import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.mvc.annotation.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * The JUnit test for the OzarkFeature class.
 *
 * @author Florian Hirsch
 */
@RunWith(CdiRunner.class)
public class OzarkFeatureTest {

	private OzarkFeature ozarkFeature = new OzarkFeature();

	@Test
	public void isController() {
		assertThat(ozarkFeature.isController(FirstController.class), is(true));
		assertThat(ozarkFeature.isController(SecondController.class), is(true));
		assertThat(ozarkFeature.isController(NoController.class), is(false));
	}

	@Test
	public void hasUnsupportedTypes() {
		assertThat(ozarkFeature.hasUnsupportedTypes(Collections.singleton(StatelessController.class)), is(true));
		assertThat(ozarkFeature.hasUnsupportedTypes(Collections.singleton(StatefulController.class)), is(true));
		assertThat(ozarkFeature.hasUnsupportedTypes(Collections.singleton(ManagedBeanController.class)), is(true));
		assertThat(ozarkFeature.hasUnsupportedTypes(new HashSet<>(Arrays.asList(FirstController.class, StatelessController.class))), is(true));
		assertThat(ozarkFeature.hasUnsupportedTypes(Collections.singleton(FirstController.class)), is(false));
	}

	@Controller @Path("first")
	private static class FirstController {
		@GET
		public String get() { return null; }
	}

	private static class SecondController {
		@GET @Controller @Path("second")
		public String get() { return null; }
	}

	@Path("none")
	private static class NoController {
		@GET
		public String get() { return null; }
	}

	@Stateless
	@Controller @Path("stateless")
	private static class StatelessController {
		@GET
		public String get() { return null; }
	}

	@Stateful
	@Controller @Path("stateful")
	private static class StatefulController {
		@GET
		public String get() { return null; }
	}

	@ManagedBean
	@Controller @Path("managed-bean")
	private static class ManagedBeanController {
		@GET
		public String get() { return null; }
	}

}
