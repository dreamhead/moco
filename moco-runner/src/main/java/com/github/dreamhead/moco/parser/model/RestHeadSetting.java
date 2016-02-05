package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestHeadSetting extends RestBaseSetting {
    private String id;

    public static Function<RestHeadSetting, RestSetting> toHeadSetting() {
        return new Function<RestHeadSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestHeadSetting setting) {
                return MocoRest.head(setting.id).response(setting.getResponseHandler());
            }
        };
    }
}
