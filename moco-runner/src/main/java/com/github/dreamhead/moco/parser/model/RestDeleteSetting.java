package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSettingBuilder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestDeleteSetting extends RestSingleSetting {
    @Override
    protected RestSettingBuilder startRestSetting() {
        if (hasId()) {
            return MocoRest.delete(id());
        }

        throw new IllegalArgumentException("Delete ID is missing");
    }
}
