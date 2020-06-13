package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.setting.Setting;
import com.google.common.collect.ImmutableList;

public interface SettingFetcher<T extends ResponseSetting> {
    ImmutableList<Setting<T>> getSettings();
    Setting<T> getAnySetting();
}

