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

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
abstract class AbstractTool implements Tool {
    protected final String name;
    protected final Map<String, Object> extraProperties = new LinkedHashMap<>();
    protected Boolean enabled;
    protected boolean enabledSet;
    protected Path templateDirectory;

    protected AbstractTool(String name) {
        this.name = name;
    }

    void setAll(AbstractTool tool) {
        this.enabled = tool.enabled;
        this.enabledSet = tool.enabledSet;
        this.templateDirectory = tool.templateDirectory;
        setExtraProperties(tool.extraProperties);
    }

    @Override
    public Boolean isEnabled() {
        return enabled != null && enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabledSet = true;
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabledSet() {
        return enabledSet;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Path getTemplateDirectory() {
        return templateDirectory;
    }

    @Override
    public void setTemplateDirectory(Path templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    @Override
    public Map<String, Object> getExtraProperties() {
        return extraProperties;
    }

    @Override
    public void setExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties.clear();
        this.extraProperties.putAll(extraProperties);
    }

    @Override
    public void addExtraProperties(Map<String, Object> extraProperties) {
        this.extraProperties.putAll(extraProperties);
    }

    public boolean isSet() {
        return enabledSet ||
            null != templateDirectory ||
            !extraProperties.isEmpty();
    }
}
