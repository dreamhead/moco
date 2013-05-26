package com.github.dreamhead.moco.config;

import com.github.dreamhead.moco.MocoConfig;

public class MocoContextConfig implements MocoConfig {
    private final String context;

    public MocoContextConfig(String context) {
        this.context = context;
    }

    @Override
    public boolean isFor(String id) {
        return "uri".equalsIgnoreCase(id);
    }

    @Override
    public String apply(String uri) {
        return context + uri;
    }
}
