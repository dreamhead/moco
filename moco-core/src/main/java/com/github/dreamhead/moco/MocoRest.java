package com.github.dreamhead.moco;

import com.github.dreamhead.moco.rest.RestSetting;

public final class MocoRest {
    public static RestSetting get(final String id, final ResponseHandler handler) {
        return new RestSetting(id, handler);
    }

    private MocoRest() {
    }
}
