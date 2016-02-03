package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestPostSetting extends RestBaseSetting {
    public static Function<RestPostSetting, RestSetting> toPostSetting() {
        return new Function<RestPostSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestPostSetting postSetting) {
                return MocoRest.post().response(postSetting.getResponseHandler());
            }
        };
    }
}
