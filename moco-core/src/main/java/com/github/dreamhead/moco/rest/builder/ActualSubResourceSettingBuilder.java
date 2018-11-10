package com.github.dreamhead.moco.rest.builder;

import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.rest.SubResourceSetting;

import static com.github.dreamhead.moco.rest.RestIds.checkResourceName;
import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ActualSubResourceSettingBuilder
        implements SubResourceSettingBuilder, NamedSubResourceSettingBuilder {
    private final RestIdMatcher id;
    private String name;

    public ActualSubResourceSettingBuilder(final RestIdMatcher id) {
        this.id = id;
    }

    @Override
    public NamedSubResourceSettingBuilder name(final String name) {
        this.name = checkResourceName(name);
        return this;
    }

    @Override
    public RestSetting settings(final RestSetting restSetting, final RestSetting... restSettings) {
        return new SubResourceSetting(this.id, this.name, asIterable(
                checkNotNull(restSetting, "Rest setting should not be null"),
                checkNotNull(restSettings, "Rest settings should not be null")));
    }
}
