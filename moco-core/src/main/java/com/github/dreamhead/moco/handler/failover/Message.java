package com.github.dreamhead.moco.handler.failover;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public abstract class Message {
    protected String version;
    protected String content;
    protected Map<String, String> headers = newHashMap();

    public void setVersion(String version) {
        this.version = version;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
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

    public boolean match(Message that) {
        return doMatch(version, that.version)
                && doMatch(content, that.content)
                && doMatch(headers, that.headers);
    }

    protected boolean doMatch(Map<String, String> thisField, Map<String, String> thatField) {
        if (thisField == null) {
            return true;
        }

        for (Map.Entry<String, String> entry : thisField.entrySet()) {
            if (!doMatch(entry.getValue(), thatField.get(entry.getKey()))) {
                return false;
            }
        }

        return true;
    }

    protected boolean doMatch(String thisField, String thatField) {
        return Strings.isNullOrEmpty(thisField) || thisField.equals(thatField);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version, content, headers);
    }
}
