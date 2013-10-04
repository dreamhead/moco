package com.github.dreamhead.moco.model;

import com.google.common.base.Objects;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public abstract class DumpMessage {
    protected String version;
    protected String content;
    protected Map<String, String> headers = newHashMap();

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
