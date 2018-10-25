package com.github.dreamhead.moco.rest.builder;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseBase;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.github.dreamhead.moco.internal.AbstractResponseBase;
import com.github.dreamhead.moco.rest.RestAllSetting;
import com.github.dreamhead.moco.rest.RestSingleSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.handler.AndResponseHandler.and;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class RestSettingBuilders extends AbstractResponseBase<RestSetting>
        implements RestSettingBuilder, ResponseBase<RestSetting> {
    protected abstract RestSetting createSetting(Optional<RequestMatcher> matcher, ResponseHandler handler);

    private RequestMatcher matcher;

    @Override
    public final ResponseBase<RestSetting> request(final RequestMatcher matcher) {
        this.matcher = checkNotNull(matcher, "Request matcher should not be null");
        return this;
    }

    @Override
    public final RestSetting response(final ResponseHandler handler, final ResponseHandler... handlers) {
        return createSetting(Optional.fromNullable(matcher),
                and(checkNotNull(handler, "Response handler should not be null"),
                        checkNotNull(handlers, "Response handlers should not be null")));
    }

    public static RestSettingBuilder single(final HttpMethod method, final RestIdMatcher id) {
        return new RestSettingBuilders() {
            @Override
            protected RestSetting createSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
                return new RestSingleSetting(method, id, matcher, handler);
            }
        };
    }

    public static RestSettingBuilder all(final HttpMethod method) {
        return new RestSettingBuilders() {
            @Override
            protected RestSetting createSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
                return new RestAllSetting(method, matcher, handler);
            }
        };
    }
}
