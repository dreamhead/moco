package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public final class HttpHeader implements ResponseElement, ConfigApplier<HttpHeader> {
    private String name;
    private Resource value;

    public HttpHeader(final String name, final Resource value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Resource getValue() {
        return value;
    }

    @Override
    public HttpHeader apply(final MocoConfig config) {
        Resource applied = this.value.apply(config);
        if (applied.equals(value)) {
            return this;
        }

        return new HttpHeader(name, applied);
    }
}
