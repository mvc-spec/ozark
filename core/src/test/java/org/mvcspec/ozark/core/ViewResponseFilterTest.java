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
package org.mvcspec.ozark.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Test for ViewResponseFilter
 *
 * @author Dmytro Maidaniuk
 */
@RunWith(value = Parameterized.class)
public class ViewResponseFilterTest {

    private String viewName;

    private String defaultExtension;

    private String expectedViewName;

    private Class<? extends Exception> expectedException;

    public ViewResponseFilterTest(String viewName,
                                  String defaultExtension,
                                  String expectedViewName,
                                  Class<? extends Exception> expectedException) {
        this.viewName = viewName;
        this.defaultExtension = defaultExtension;
        this.expectedViewName = expectedViewName;
        this.expectedException = expectedException;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"main.jsp", "jsp", "main.jsp", null},
            {"main", "jsp", "main.jsp", null},
            {"main", null, "main", null},
            {"main", "", "main", null},
            {"redirect:some.jsp", "jsp", "redirect:some.jsp", null},
            {"react:some", "jsp", "react:some", null},
            {"main.", "jsp", null, IllegalArgumentException.class}
        });
    }

    @Test
    public void testAppendExtensionIfRequired() {
        if (expectedException != null) {
            exception.expect(expectedException);
        }
        String actualViewName = ViewResponseFilter.appendExtensionIfRequired(viewName, defaultExtension);
        assertEquals(expectedViewName, actualViewName);
    }

}
