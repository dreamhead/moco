package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSettingBuilder;
import com.google.common.base.Strings;

import static com.github.dreamhead.moco.parser.model.RestIds.asIdMatcher;

public abstract class RestSingleSetting extends RestBaseSetting {
    private String id;

    protected abstract RestSettingBuilder doStartRestSetting();

    protected boolean hasId() {
        return !Strings.isNullOrEmpty(id);
    }

    protected RestIdMatcher id() {
        return asIdMatcher(this.id);
    }

    protected boolean isIdRequired() {
        return false;
    }

    @Override
    protected RestSettingBuilder startRestSetting() {
        if (isIdRequired() && !hasId()) {
            throw new IllegalArgumentException("Required id is missing");
        }

        return doStartRestSetting();
    }
}
