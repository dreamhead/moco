package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSettingBuilder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestGetSetting extends RestBaseSetting {
    private String id;

    @Override
    protected RestSettingBuilder startRestSetting() {
        return MocoRest.get(id);
    }
}
