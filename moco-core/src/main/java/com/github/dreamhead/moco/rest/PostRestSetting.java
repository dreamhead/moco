package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Optional;

public class PostRestSetting extends RestAllSetting {
    public PostRestSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(matcher, handler);
    }

    public static RestSettingBuilder builder() {
        return new PostSettingBuilder();
    }

    private static class PostSettingBuilder extends BaseRestSettingBuilder {
        @Override
        protected RestSetting createSetting(final Optional<RequestMatcher> matcher,
                                            final ResponseHandler handler) {
            return new PostRestSetting(matcher, handler);
        }
    }
}
