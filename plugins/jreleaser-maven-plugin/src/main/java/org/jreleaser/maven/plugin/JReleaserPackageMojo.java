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
import org.jreleaser.model.JReleaserContext;
import org.jreleaser.workflow.Workflows;

/**
 * Package all distributions.
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@Mojo(name = "package")
public class JReleaserPackageMojo extends AbstractJReleaserMojo {
    /**
     * The name of the distribution.
     */
    @Parameter(property = "jreleaser.distribution.name")
    private String distributionName;

    /**
     * The name of the tool.
     */
    @Parameter(property = "jreleaser.tool.name")
    private String toolName;

    /**
     * Skip execution.
     */
    @Parameter(property = "jreleaser.package.skip")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Banner.display(project, getLog());
        if (skip) {
            getLog().info("Execution has been explicitly skipped.");
            return;
        }

        JReleaserContext context = createContext();
        context.setDistributionName(distributionName);
        context.setToolName(toolName);
        Workflows.packageRelease(context).execute();
    }
}
