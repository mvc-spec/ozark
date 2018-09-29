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
package org.mvcspec.ozark.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Gregor Tudan
 */
@RunWith(Arquillian.class)
public class ThymeleafIT {

    private static final String WEB_INF_SRC = "src/main/resources/thymeleaf/";

    @ArquillianResource
    private URL baseURL;

    @Drone
    private WebDriver webDriver;

    @Deployment(testable = false, name = "thymeleaf")
    public static Archive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
            .addPackage("org.mvcspec.ozark.test.thymeleaf")
            .addAsWebInfResource(new FileAsset(Paths.get(WEB_INF_SRC).resolve("views/hello.html").toFile()), "views/hello.html")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibraries(
                Maven.configureResolver().workOffline()
                    .withClassPathResolution(true)
                    .loadPomFromFile("pom.xml", System.getProperty("testsuite.profile"))
                    .importCompileAndRuntimeDependencies()
                    .resolve().withTransitivity().asFile()
            );
    }

    @Test
    @RunAsClient
    public void test() {
        webDriver.get(baseURL + "resources/hello?user=mvc");
        WebElement h1 = webDriver.findElement(By.tagName("h1"));
        assertNotNull(h1);
        assertTrue(h1.getText().contains("mvc"));
    }
}
