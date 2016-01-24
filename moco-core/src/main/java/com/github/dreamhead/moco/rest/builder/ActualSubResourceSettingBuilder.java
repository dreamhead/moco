package com.github.dreamhead.moco.rest.builder;

import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.rest.SubResourceSetting;

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
    public RestSetting settings(final RestSetting... settings) {
        return new SubResourceSetting(this.id, this.name,
                checkNotNull(settings, "Rest settings should not be null"));
    }
}
