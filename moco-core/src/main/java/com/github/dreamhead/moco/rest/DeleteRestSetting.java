package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Optional;

public class DeleteRestSetting extends RestSingleSetting {
    public DeleteRestSetting(final RestIdMatcher id, final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(id, matcher, handler);
    }

    public static RestSettingBuilder builder(final RestIdMatcher idMatcher) {
        return new DeleteRestSettingBuilder(idMatcher);
    }

    private static final class DeleteRestSettingBuilder extends BaseRestSettingBuilder {
        private final RestIdMatcher id;

        private DeleteRestSettingBuilder(final RestIdMatcher id) {
            this.id = id;
        }

        @Override
        protected RestSetting createSetting(final Optional<RequestMatcher> matcher,
                                            final ResponseHandler handler) {
            return new DeleteRestSetting(id, matcher, handler);
        }
    }
}
