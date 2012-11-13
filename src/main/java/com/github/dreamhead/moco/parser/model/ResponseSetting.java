package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

import java.util.Map;

public class ResponseSetting {
    private String text;
    private String file;
    private String status;
    private Map<String, String> headers;

    public String getText() {
        return text;
    }

    public String getFile() {
        return file;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("text", text).add("file", file).add("status", status).add("headers", headers).toString();
    }
}
