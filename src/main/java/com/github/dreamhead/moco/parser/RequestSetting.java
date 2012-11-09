package com.github.dreamhead.moco.parser;

import com.google.common.base.Objects;

public class RequestSetting {
    private String text;
    private String uri;
    private String file;

    public String getUri() {
        return uri;
    }

    public String getText() {
        return text;
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("URI", uri).add("text", text).add("file", file).toString();
    }
}
