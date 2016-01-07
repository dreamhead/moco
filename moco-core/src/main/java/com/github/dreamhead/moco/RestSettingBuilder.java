package com.github.dreamhead.moco;

public interface RestSettingBuilder extends RestSettingResponseBuilder {
    RestSettingResponseBuilder request(final RequestMatcher matcher);
}
