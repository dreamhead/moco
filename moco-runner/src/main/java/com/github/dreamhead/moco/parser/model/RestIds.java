package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.rest.RestIdMatchers;

public final class RestIds {
    public static RestIdMatcher asIdMatcher(final String id) {
        if ("*".equals(id)) {
            return MocoRest.anyId();
        }

        return RestIdMatchers.eq(id);
    }

    private RestIds() {
    }
}
