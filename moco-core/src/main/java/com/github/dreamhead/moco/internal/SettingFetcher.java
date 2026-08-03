package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseSetting;

import java.util.List;
import com.github.dreamhead.moco.setting.Setting;

public interface SettingFetcher<T extends ResponseSetting> {
    List<Setting<T>> getSettings();
    Setting<T> getAnySetting();
}

