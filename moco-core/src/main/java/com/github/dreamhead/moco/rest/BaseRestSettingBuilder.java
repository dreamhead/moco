package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.handler.AndResponseHandler.and;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseRestSettingBuilder implements RestSettingBuilder {
    protected abstract RestSetting createSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler);

    private RequestMatcher matcher;

    @Override
    public RestSettingBuilder request(final RequestMatcher matcher) {
        this.matcher = checkNotNull(matcher, "Request matcher should not be null");
        return this;
    }

    @Override
    public RestSetting response(final ResponseHandler handler, final ResponseHandler... handlers) {
        return createSetting(Optional.fromNullable(matcher),
                and(checkNotNull(handler, "Post response handler should not be null"),
                        checkNotNull(handlers, "Post response handler should not be null")));
    }

    public static RestSettingBuilder single(final HttpMethod method, final RestIdMatcher id) {
        return new BaseRestSettingBuilder() {
            @Override
            protected RestSetting createSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
                return new RestSingleSetting(method, id, matcher, handler);
            }
        };
    }

    public static RestSettingBuilder all(final HttpMethod method) {
        return new BaseRestSettingBuilder() {
            @Override
            protected RestSetting createSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
                return new RestAllSetting(method, matcher, handler);
            }
        };
    }
}
