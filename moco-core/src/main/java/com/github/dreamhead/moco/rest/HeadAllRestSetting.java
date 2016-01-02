package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Optional;

public class HeadAllRestSetting extends RestAllSetting {
    public HeadAllRestSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(matcher, handler);
    }

    public static RestSettingBuilder builder() {
        return new HeadRestAllSettingBuilder();
    }

    private static class HeadRestAllSettingBuilder extends BaseRestSettingBuilder {
        @Override
        protected RestSetting createSetting(final Optional<RequestMatcher> matcher,
                                            final ResponseHandler handler) {
            return new HeadAllRestSetting(matcher, handler);
        }
    }
}
