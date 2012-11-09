package com.github.dreamhead.moco;

public abstract class Setting extends ResponseSetting {
    protected final RequestMatcher matcher;

    protected Setting(RequestMatcher matcher) {
        this.matcher = matcher;
    }
}
