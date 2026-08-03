package com.github.dreamhead.moco.rest.builder;

import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.rest.SubResourceSetting;

import static com.github.dreamhead.moco.rest.RestIds.checkResourceName;
import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static java.util.Objects.requireNonNull;

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
                requireNonNull(restSetting, "Rest setting should not be null"),
                requireNonNull(restSettings, "Rest settings should not be null")));
    }
}
