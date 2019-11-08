package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.ResponseBase;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

public abstract class RestBaseSetting {
    private RequestSetting request;
    private ResponseSetting response;

    protected abstract RestSettingBuilder startRestSetting();

    final RestSetting toRestSetting() {
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

    public static Iterable<RestSetting> asRestSetting(final List<? extends RestBaseSetting> setting) {
        if (setting == null || setting.isEmpty()) {
            return ImmutableList.of();
        }

        return setting.stream()
                .map(RestBaseSetting::toRestSetting)
                .collect(Collectors.toList());
    }

    protected final MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("request", request)
                .add("response", response);
    }
}
