package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

public class RequestSetting {
    private String text;
    private String uri;
    private String file;
    private String method;

    public String getUri() {
        return uri;
    }

    public String getText() {
        return text;
    }

    public String getFile() {
        return file;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("URI", uri).add("text", text).add("file", file).toString();
    }
}
