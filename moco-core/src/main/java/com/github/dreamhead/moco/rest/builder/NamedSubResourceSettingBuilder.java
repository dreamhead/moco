package com.github.dreamhead.moco.rest.builder;

import com.github.dreamhead.moco.RestSetting;

public interface NamedSubResourceSettingBuilder {
    RestSetting settings(final RestSetting restSetting, final RestSetting... restSettings);
}
