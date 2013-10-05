package com.github.dreamhead.moco.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

public abstract class DumpMessage {
    protected final String version;
    protected final String content;
    protected final ImmutableMap<String, String> headers;

    protected DumpMessage(String version, String content, ImmutableMap<String, String> headers) {
        this.version = version;
        this.headers = headers;
        this.content = content;
    }

    public String getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public ImmutableMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version, content, headers);
    }
}
