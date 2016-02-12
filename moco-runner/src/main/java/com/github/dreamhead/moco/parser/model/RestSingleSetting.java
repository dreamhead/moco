package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.rest.RestIdMatchers;

public abstract class RestSingleSetting extends RestBaseSetting {
    private String id;

    protected RestIdMatcher id() {
        if ("*".equals(id)) {
            return MocoRest.anyId();
        }

        return RestIdMatchers.eq(this.id);
    }
}
