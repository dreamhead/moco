package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.RestIdMatcher;

public abstract class RestSingleSetting extends RestBaseSetting {
    private String id;

    protected RestIdMatcher id() {
        return RestIds.asIdMatcher(this.id);
    }
}
