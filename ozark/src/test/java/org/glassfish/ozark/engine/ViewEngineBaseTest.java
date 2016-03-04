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
package org.glassfish.ozark.engine;

import org.easymock.EasyMock;
import org.junit.Test;

import javax.mvc.engine.ViewEngine;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.ws.rs.core.Configuration;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * The JUnit test for the ViewEngineBase class.
 *
 * @author Florian Hirsch
 */
public class ViewEngineBaseTest {

	private final ViewEngineBase viewEngineBase = new ViewEngineBase() {
		@Override
		public boolean supports(String view) {
			return false;
		}
		@Override
		public void processView(ViewEngineContext context) throws ViewEngineException {

		}
	};

	@Test
	public void resolveView() {
		ViewEngineContext ctx = EasyMock.createMock(ViewEngineContext.class);
		Configuration config = EasyMock.createMock(Configuration.class);
		expect(config.getProperty(eq(ViewEngine.VIEW_FOLDER))).andReturn(null);
		expect(ctx.getConfiguration()).andReturn(config);
		expect(ctx.getView()).andReturn("index.jsp");
		expect(ctx.getView()).andReturn("/somewhere/else/index.jsp");
		replay(ctx, config);
		assertThat(viewEngineBase.resolveView(ctx), is("/WEB-INF/views/index.jsp"));
		assertThat(viewEngineBase.resolveView(ctx), is("/somewhere/else/index.jsp"));
		verify(ctx, config);
	}

	@Test
	public void resolveViewCustomFolder() {
		ViewEngineContext ctx = EasyMock.createMock(ViewEngineContext.class);
		Configuration config = EasyMock.createMock(Configuration.class);
		expect(config.getProperty(eq(ViewEngine.VIEW_FOLDER))).andReturn("/somewhere/else");
		expect(ctx.getConfiguration()).andReturn(config);
		expect(ctx.getView()).andReturn("index.jsp");
		replay(ctx, config);
		assertThat(viewEngineBase.resolveView(ctx), is("/somewhere/else/index.jsp"));
		verify(ctx, config);
	}

}
