package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSettingBuilder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class RestPostSetting extends RestBaseSetting {
    @Override
    protected RestSettingBuilder startRestSetting() {
        return MocoRest.post();
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
