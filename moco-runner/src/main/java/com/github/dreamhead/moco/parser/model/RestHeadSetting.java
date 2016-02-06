package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSettingBuilder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestHeadSetting extends RestBaseSetting {
    private String id;

    @Override
    protected RestSettingBuilder startRestSetting() {
        return MocoRest.head(id);
    }
}
