package com.github.dreamhead.moco.model;

import com.google.common.base.Objects;

import java.util.Map;

public abstract class DumpMessage {
    protected final String version;
    protected final String content;
    protected final Map<String, String> headers;

    protected DumpMessage(String version, String content, Map<String, String> headers) {
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

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version, content, headers);
    }
}
