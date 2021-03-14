/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jreleaser.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jreleaser.announce.Announcers;
import org.jreleaser.maven.plugin.internal.JReleaserModelConfigurer;
import org.jreleaser.maven.plugin.internal.JReleaserModelConverter;
import org.jreleaser.model.JReleaserModel;
import org.jreleaser.model.JReleaserModelValidator;
import org.jreleaser.model.announcer.spi.AnnounceException;

import java.util.List;

@Mojo(name = "announce")
public class AnnounceMojo extends AbstractJReleaserMojo {
    /**
     * Skip execution.
     */
    @Parameter(property = "jreleaser.announce.skip")
    private boolean skip;

    @Parameter(property = "jreleaser.announce.dryrun")
    private boolean dryrun;

    @Parameter(required = true)
    private Jreleaser jreleaser;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Banner.display(project, getLog());
        if (skip) return;

        JReleaserModel jreleaserModel = JReleaserModelConverter.convert(jreleaser);
        JReleaserModelConfigurer.configure(jreleaserModel, project);
        List<String> errors = JReleaserModelValidator.validate(getLogger(), project.getBasedir().toPath(), jreleaserModel);
        if (!errors.isEmpty()) {
            getLog().error("== JReleaser ==");
            errors.forEach(getLog()::error);
            throw new MojoExecutionException("JReleaser for project " + project.getArtifactId() + " has not been properly configured.");
        }

        try {
            Announcers.announce(getLogger(), jreleaserModel, project.getBasedir().toPath(), dryrun);
        } catch (AnnounceException e) {

            throw new MojoExecutionException("Unexpected error", e);
        }
    }
}
