package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseBase;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

public abstract class RestBaseSetting {
    protected RequestSetting request;
    protected ResponseSetting response;

    protected abstract RestSettingBuilder startRestSetting();

    protected boolean hasRequest() {
        return request != null;
    }

    protected boolean hasResponse() {
        return response != null;
    }

    protected ResponseHandler getResponseHandler() {
        return response.getResponseHandler();
    }

    protected RequestMatcher getRequestMatcher() {
        return request.getRequestMatcher();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("request", request)
                .add("response", response)
                .toString();
    }

    public static <T extends RestBaseSetting> Function<T, RestSetting> toSetting() {
        return new Function<T, RestSetting>() {
            @Override
            public RestSetting apply(final T setting) {
                if (!setting.hasResponse()) {
                    throw new IllegalArgumentException("Response is expected in rest setting");
                }

                return getRestSettingBuilder(setting).response(setting.getResponseHandler());
            }
        };
    }

    private static <T extends RestBaseSetting> ResponseBase<RestSetting> getRestSettingBuilder(final T setting) {
        RestSettingBuilder builder = setting.startRestSetting();
        if (setting.hasRequest()) {
            return builder.request(setting.getRequestMatcher());
        }

        return builder;
    }
}
