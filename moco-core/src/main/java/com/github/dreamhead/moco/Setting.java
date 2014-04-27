package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ResponseSettingConfiguration;

public abstract class Setting extends ResponseSettingConfiguration {
    protected final RequestMatcher matcher;

    protected Setting(final RequestMatcher matcher) {
        this.matcher = matcher;
    }
}
