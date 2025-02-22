/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2023 The JReleaser authors.
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
package org.jreleaser.sdk.mastodon;

import org.jreleaser.bundle.RB;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.spi.announce.AnnounceException;
import org.jreleaser.model.spi.announce.Announcer;
import org.jreleaser.mustache.TemplateContext;
import org.jreleaser.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jreleaser.model.Constants.KEY_PREVIOUS_TAG_NAME;
import static org.jreleaser.model.Constants.KEY_TAG_NAME;
import static org.jreleaser.mustache.MustacheUtils.applyTemplates;
import static org.jreleaser.mustache.Templates.resolveTemplate;
import static org.jreleaser.util.StringUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 0.4.0
 */
@org.jreleaser.infra.nativeimage.annotations.NativeImage
public class MastodonAnnouncer implements Announcer<org.jreleaser.model.api.announce.MastodonAnnouncer> {
    private final JReleaserContext context;
    private final org.jreleaser.model.internal.announce.MastodonAnnouncer mastodon;

    public MastodonAnnouncer(JReleaserContext context) {
        this.context = context;
        this.mastodon = context.getModel().getAnnounce().getMastodon();
    }

    @Override
    public org.jreleaser.model.api.announce.MastodonAnnouncer getAnnouncer() {
        return mastodon.asImmutable();
    }

    @Override
    public String getName() {
        return org.jreleaser.model.api.announce.MastodonAnnouncer.TYPE;
    }

    @Override
    public boolean isEnabled() {
        return mastodon.isEnabled();
    }

    @Override
    public void announce() throws AnnounceException {
        List<String> statuses = new ArrayList<>();

        if (isNotBlank(mastodon.getStatusTemplate())) {
            TemplateContext props = new TemplateContext();
            context.getModel().getRelease().getReleaser().fillProps(props, context.getModel());
            Arrays.stream(mastodon.getResolvedStatusTemplate(context, props)
                    .split(System.lineSeparator()))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .forEach(statuses::add);
        }
        if (statuses.isEmpty() && !mastodon.getStatuses().isEmpty()) {
            statuses.addAll(mastodon.getStatuses());
            mastodon.getStatuses().stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .forEach(statuses::add);
        }
        if (statuses.isEmpty()) {
            statuses.add(mastodon.getStatus());
        }

        for (int i = 0; i < statuses.size(); i++) {
            String status = getResolvedMessage(context, statuses.get(i));
            context.getLogger().info(RB.$("mastodon.toot"), status);
            context.getLogger().debug(RB.$("mastodon.toot.size"), status.length());
            statuses.set(i, status);
        }

        try {
            MastodonSdk sdk = MastodonSdk.builder(context.getLogger())
                .host(mastodon.getHost())
                .accessToken(mastodon.getAccessToken())
                .connectTimeout(mastodon.getConnectTimeout())
                .readTimeout(mastodon.getReadTimeout())
                .dryrun(context.isDryrun())
                .build();
            sdk.toot(statuses);
        } catch (MastodonException e) {
            throw new AnnounceException(e);
        }
    }

    private String getResolvedMessage(JReleaserContext context, String message) {
        TemplateContext props = context.fullProps();
        applyTemplates(props, context.getModel().getAnnounce().getMastodon().resolvedExtraProperties());
        props.set(KEY_TAG_NAME, context.getModel().getRelease().getReleaser().getEffectiveTagName(context.getModel()));
        props.set(KEY_PREVIOUS_TAG_NAME, context.getModel().getRelease().getReleaser().getResolvedPreviousTagName(context.getModel()));
        return resolveTemplate(message, props);
    }
}
