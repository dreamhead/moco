package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestDeleteSetting extends RestBaseSetting {
    private String id;

    public static Function<RestDeleteSetting, RestSetting> toDeleteSetting() {
        return new Function<RestDeleteSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestDeleteSetting setting) {
                return MocoRest.delete(setting.id).response(setting.getResponseHandler());
            }
        };
    }
}
