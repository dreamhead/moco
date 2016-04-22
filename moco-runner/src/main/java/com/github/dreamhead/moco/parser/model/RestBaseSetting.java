package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseBase;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

public abstract class RestBaseSetting {
    protected RequestSetting request;
    protected ResponseSetting response;

    protected abstract RestSettingBuilder startRestSetting();

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("request", request)
                .add("response", response)
                .toString();
    }

    RestSetting toRestSetting() {
        if (response == null) {
            throw new IllegalArgumentException("Response is expected in rest setting");
        }

        return this.getRestSettingBuilder().response(response.getResponseHandler());
    }

    private ResponseBase<RestSetting> getRestSettingBuilder() {
        RestSettingBuilder builder = this.startRestSetting();
        if (request != null) {
            return builder.request(request.getRequestMatcher());
        }

        return builder;
    }

    public static <T extends RestBaseSetting> Function<T, RestSetting> toSetting() {
        return new Function<T, RestSetting>() {
            @Override
            public RestSetting apply(final T setting) {
                return setting.toRestSetting();
            }
        };
    }
}
