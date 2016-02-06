package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Function;

public abstract class RestBaseSetting {
    private ResponseSetting response;

    protected abstract RestSettingBuilder startRestSetting();

    protected ResponseHandler getResponseHandler() {
        return response.getResponseHandler();
    }

    public static <T extends RestBaseSetting> Function<T, RestSetting> toSetting() {
        return new Function<T, RestSetting>() {
            @Override
            public RestSetting apply(final T setting) {
                return setting.startRestSetting().response(setting.getResponseHandler());
            }
        };
    }
}
