package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Optional;

public class PutRestSetting extends RestSingleSetting {
    public PutRestSetting(final RestIdMatcher id,
                          final Optional<RequestMatcher> matcher,
                          final ResponseHandler handler) {
        super(id, matcher, handler);
    }

    public static RestSettingBuilder builder(final RestIdMatcher idMatcher) {
        return new PutRestSettingBuilder(idMatcher);
    }

    private static final class PutRestSettingBuilder extends BaseRestSettingBuilder {
        private final RestIdMatcher id;

        private PutRestSettingBuilder(final RestIdMatcher id) {
            this.id = id;
        }

        @Override
        protected RestSetting createSetting(final Optional<RequestMatcher> matcher,
                                            final ResponseHandler handler) {
            return new PutRestSetting(id, matcher, handler);
        }
    }
}
