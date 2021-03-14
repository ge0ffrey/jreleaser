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
package org.jreleaser.gradle.plugin.internal.dsl

import groovy.transform.CompileStatic
import org.gradle.api.internal.provider.Providers
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.jreleaser.gradle.plugin.dsl.Twitter

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class TwitterImpl extends AbstractAnnouncer implements Twitter {
    final Property<String> consumerKey
    final Property<String> consumerSecret
    final Property<String> accessToken
    final Property<String> accessTokenSecret
    final Property<String> status

    @Inject
    TwitterImpl(ObjectFactory objects) {
        super(objects)
        consumerKey = objects.property(String).convention(Providers.notDefined())
        consumerSecret = objects.property(String).convention(Providers.notDefined())
        accessToken = objects.property(String).convention(Providers.notDefined())
        accessTokenSecret = objects.property(String).convention(Providers.notDefined())
        status = objects.property(String).convention(Providers.notDefined())
    }

    @Override
    @Internal
    boolean isSet() {
        return super.isSet() ||
            consumerKey.present ||
            consumerSecret.present ||
            accessToken.present ||
            accessTokenSecret.present ||
            status.present
    }

    org.jreleaser.model.Twitter toModel() {
        org.jreleaser.model.Twitter twitter = new org.jreleaser.model.Twitter()
        twitter.enabled = enabled.getOrElse(isSet())
        twitter.consumerKey = consumerKey.orNull
        twitter.consumerSecret = consumerSecret.orNull
        twitter.accessToken = accessToken.orNull
        twitter.accessTokenSecret = accessTokenSecret.orNull
        twitter.status = status.orNull
        twitter
    }
}
