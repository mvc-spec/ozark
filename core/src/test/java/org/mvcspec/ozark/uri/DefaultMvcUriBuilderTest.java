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
package org.mvcspec.ozark.uri;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * <p>The test for {@link DefaultMvcUriBuilder}.</p>
 *
 * @author Florian Hirsch
 */
public class DefaultMvcUriBuilderTest {

    private static UriTemplate basePath = UriTemplate.fromTemplate("/base-path").build();

    private static UriTemplate pathParams = UriTemplate.fromTemplate("/path-params/{p1}/{p2}/{p3}").build(); //

    private static UriTemplate identicalPathParams = UriTemplate.fromTemplate("/path-params/{p1}/{p1}").build(); //

    private static UriTemplate queryParams = UriTemplate.fromTemplate("/query-params") //
        .queryParam("q1").queryParam("q2").queryParam("q3").build(); //

    private static UriTemplate matrixParams = UriTemplate.fromTemplate("/matrix-params") //
        .matrixParam("m1").matrixParam("m2").matrixParam("m3").build();

    private static UriTemplate allParams = UriTemplate.fromTemplate("/params/{p1}/{p2}/{p3}") //
        .matrixParam("m1").matrixParam("m2").matrixParam("m3") //
        .queryParam("q1").queryParam("q2").queryParam("q3").build(); //

    @Test
    public void shouldBuildWithoutParams() {
        assertThat(new DefaultMvcUriBuilder(basePath).build().toString(), equalTo("/base-path"));
    }

    @Test
    public void shouldHandlePathParams() {
        assertThat(new DefaultMvcUriBuilder(pathParams).param("p1", 1) //
                .param("p2", "2").param("p3", 3).build().toString(), //
            equalTo("/path-params/1/2/3")); //
    }

    @Test
    public void shouldHandleQueryParams() {
        assertThat(new DefaultMvcUriBuilder(queryParams) //
            .param("q1", 7).param("q2", "8").param("q3", 9).build().toString(), //
            equalTo("/query-params?q1=7&q2=8&q3=9"));
    }

    @Test
    public void shouldHandleMatrixParams() {
        assertThat(new DefaultMvcUriBuilder(matrixParams) //
            .param("m1", 3).param("m2", "4").param("m3", 5).build().toString(), //
            equalTo("/matrix-params;m1=3;m2=4;m3=5")); //
    }

    @Test
    public void shouldHandleMixedParams() {
        assertThat(new DefaultMvcUriBuilder(allParams).param("p1", 1).param("p2", "2") //
            .param("p3", 3).param("m1", 4).param("q1", "7").build().toString(), //
            equalTo("/params/1/2/3;m1=4?q1=7")); //
    }

    @Test
    public void shouldHandleIdenticalPathParams() {
        assertThat(new DefaultMvcUriBuilder(identicalPathParams) //
                .param("p1", 1).build().toString(), equalTo("/path-params/1/1")); //
    }

    @Test
    public void shouldHandleListOfQueryParams() {
        // two param calls
        assertThat(new DefaultMvcUriBuilder(queryParams) //
            .param("q1", 9).param("q1", "10").build().toString(), //
            equalTo("/query-params?q1=9&q1=10")); //
        // varargs
        assertThat(new DefaultMvcUriBuilder(queryParams) //
            .param("q1", 9, "10").build().toString(), //
            equalTo("/query-params?q1=9&q1=10")); //
        // list
        assertThat(new DefaultMvcUriBuilder(queryParams) //
            .param("p3", 3).param("q1", Arrays.asList(9, "10")).build().toString(), //
            equalTo("/query-params?q1=9&q1=10")); //
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForMissingPathParameters() {
        new DefaultMvcUriBuilder(allParams).param("p1", 1).build();
    }

}
