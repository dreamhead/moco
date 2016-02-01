package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestGetSetting {
    private String id;
    private ResponseSetting response;

    public static Function<RestGetSetting, RestSetting> toGetRestSetting() {
        return new Function<RestGetSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestGetSetting setting) {
                return MocoRest.get(setting.id).response(setting.response.getResponseHandler());
            }
        };
    }
}
