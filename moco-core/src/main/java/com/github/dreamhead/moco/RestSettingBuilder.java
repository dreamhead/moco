package com.github.dreamhead.moco;

public interface RestSettingBuilder extends ResponseBase<RestSetting> {
    ResponseBase<RestSetting> request(RequestMatcher matcher);
}
