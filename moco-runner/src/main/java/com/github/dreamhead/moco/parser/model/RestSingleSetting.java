package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.RestIdMatcher;

import static com.github.dreamhead.moco.parser.model.RestIds.asIdMatcher;

public abstract class RestSingleSetting extends RestBaseSetting {
    private String id;

    protected RestIdMatcher id() {
        return asIdMatcher(this.id);
    }
}
