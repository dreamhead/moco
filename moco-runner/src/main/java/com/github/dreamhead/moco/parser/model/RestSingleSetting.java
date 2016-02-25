package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.RestIdMatcher;
import com.google.common.base.Strings;

import static com.github.dreamhead.moco.parser.model.RestIds.asIdMatcher;

public abstract class RestSingleSetting extends RestBaseSetting {
    private String id;

    protected boolean hasId() {
        return !Strings.isNullOrEmpty(id);
    }

    protected RestIdMatcher id() {
        return asIdMatcher(this.id);
    }
}
