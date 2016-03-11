package com.github.dreamhead.moco.rest.builder;

import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.rest.SubResourceSetting;
import com.google.common.collect.ImmutableList;

import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public class ActualSubResourceSettingBuilder implements SubResourceSettingBuilder, NamedSubResourceSettingBuilder {
    private final RestIdMatcher id;
    private String name;

    public ActualSubResourceSettingBuilder(final RestIdMatcher id) {
        this.id = id;
    }

    @Override
    public NamedSubResourceSettingBuilder name(final String name) {
        this.name = checkNotNullOrEmpty(name, "Resource name should not be null or empty");
        return this;
    }

    @Override
    public RestSetting settings(final RestSetting restSetting, final RestSetting... restSettings) {
        return new SubResourceSetting(this.id, this.name, asIterable(
                checkNotNull(restSetting, "Rest setting should not be null"),
                checkNotNull(restSettings, "Rest settings should not be null")));
    }
}
