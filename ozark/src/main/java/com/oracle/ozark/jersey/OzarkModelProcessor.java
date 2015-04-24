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
package com.oracle.ozark.jersey;

import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;

import javax.mvc.Controller;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;

/**
 * A Jersey model processor to ensure that all controller methods whose {@code @Produces}
 * list are empty are set to "text/html", which is the default for MVC.
 *
 * @author Santiago Pericas-Geertsen
 */
public class OzarkModelProcessor implements ModelProcessor {

    @Override
    public ResourceModel processResourceModel(ResourceModel resourceModel, Configuration configuration) {
        ResourceModel.Builder rmb = new ResourceModel.Builder(false);
        resourceModel.getResources().forEach(r -> {
            rmb.addResource(processResource(r));
        });
        return rmb.build();
    }

    @Override
    public ResourceModel processSubResource(ResourceModel subResourceModel, Configuration configuration) {
        return subResourceModel;
    }

    /**
     * Updates the default {@code @Produces} list of every controller method whose list is empty.
     * The new list contains a single media type: "text/html".
     *
     * @param r resource to process.
     * @return newly updated resource.
     */
    private static Resource processResource(Resource r) {
        final boolean isControllerClass = isController(r);
        Resource.Builder rb = Resource.builder(r);
        r.getAllMethods().forEach(
                (ResourceMethod m) -> {
                    if ((isController(m) || isControllerClass) && m.getProducedTypes().isEmpty()) {
                        final ResourceMethod.Builder rmb = rb.updateMethod(m);
                        rmb.produces(MediaType.TEXT_HTML_TYPE);
                        rmb.build();
                    }
                }
        );
        r.getChildResources().forEach(cr -> {
            rb.replaceChildResource(cr, processResource(cr));
        });
        return rb.build();
    }

    /**
     * Determines if a resource method is a controller.
     *
     * @param method resource method to test.
     * @return outcome of controller test.
     */
    private static boolean isController(ResourceMethod method) {
        return method.getInvocable().getDefinitionMethod().isAnnotationPresent(Controller.class);
    }

    /**
     * Determines if a resource is a controller.
     *
     * @param resource resource to test.
     * @return outcome of controller test.
     */
    private static boolean isController(Resource resource) {
        final Boolean b1 = resource.getHandlerClasses().stream()
                .map(c -> c.isAnnotationPresent(Controller.class))
                .reduce(Boolean.FALSE, Boolean::logicalOr);
        final Boolean b2 = resource.getHandlerInstances().stream()
                .map(o -> o.getClass().isAnnotationPresent(Controller.class))
                .reduce(Boolean.FALSE, Boolean::logicalOr);
        return b1 || b2;
    }
}
