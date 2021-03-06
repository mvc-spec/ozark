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
package org.mvcspec.ozark.tck;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepository;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenUpdatePolicy;
import org.mvcspec.tck.api.BaseArchiveProvider;

import java.io.File;

/**
 * Base class for BaseArchiveProvider implementations
 *
 * @author Christian Kaltepoth
 */
public abstract class AbstractArchiveProvider implements BaseArchiveProvider {

    private final MavenRemoteRepository sonatypeRepo;

    public AbstractArchiveProvider() {

        sonatypeRepo = MavenRemoteRepositories.createRemoteRepository(
            "sonatype-oss-snapshots",
            "https://oss.sonatype.org/content/repositories/snapshots",
            "default"
        );

        // prevent weird errors logged during test execution
        sonatypeRepo.setUpdatePolicy(MavenUpdatePolicy.UPDATE_POLICY_ALWAYS);

    }

    protected File[] resolveMvcSpecJar() {

        // Get this one from the local repo, central or the snapshot report
        return Maven.configureResolver()
            .withRemoteRepo(sonatypeRepo)
            .loadPomFromFile("pom.xml")  // see <dependencyManagment> for artifact versions
            .resolve("javax.mvc:javax.mvc-api")
            .withTransitivity()
            .asFile();

    }

    protected File[] resolveOzarkJersey() {
        return resolveOzarkModule("org.mvc-spec.ozark", "ozark-jersey");
    }

    protected File[] resolveOzarkRestEasy() {
        return resolveOzarkModule("org.mvc-spec.ozark", "ozark-resteasy");
    }

    protected File[] resolveOzarkCxf() {
        return resolveOzarkModule("org.mvc-spec.ozark", "ozark-cxf");
    }

    protected File[] resolveOzarkCore() {
        return resolveOzarkModule("org.mvc-spec.ozark", "ozark-core");
    }

    protected File[] resolveOzarkModule(String groupId, String artifactId) {

        // will go offline because we only want to the local repo
        return Maven.configureResolver()
            .workOffline()
            .loadPomFromFile("pom.xml")  // see <dependencyManagment> for artifact versions
            .resolve(groupId + ":" + artifactId)
            .withTransitivity()
            .asFile();

    }


}
