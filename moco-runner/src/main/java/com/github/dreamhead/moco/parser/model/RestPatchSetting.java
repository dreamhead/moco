package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestPatchSetting extends RestBaseSetting {
    private String id;

    public static Function<RestPatchSetting, RestSetting> toPatchSetting() {
        return new Function<RestPatchSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestPatchSetting setting) {
                return MocoRest.patch(setting.id).response(setting.getResponseHandler());
            }
        };
    }
}
