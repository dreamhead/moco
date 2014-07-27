package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.HttpResponseSettingConfiguration;

public abstract class HttpSetting extends HttpResponseSettingConfiguration {
    protected final RequestMatcher matcher;

    protected HttpSetting(final RequestMatcher matcher) {
        this.matcher = matcher;
    }
}
