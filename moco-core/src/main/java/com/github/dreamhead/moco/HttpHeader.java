package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public class HttpHeader {
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
}
