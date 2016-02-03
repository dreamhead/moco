package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestPutSetting extends RestBaseSetting {
    private String id;

    public static Function<RestPutSetting, RestSetting> toPutSetting() {
        return new Function<RestPutSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestPutSetting putSetting) {
                return MocoRest.put(putSetting.id).response(putSetting.getResponseHandler());
            }
        };
    }

}
