package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RestSetting;

public interface NamedSubResourceSettingBuilder {
    RestSetting settings(RestSetting... restSettings);
}
