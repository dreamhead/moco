package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

public class ResponseSetting {
    private String text;
    private String file;
    private String status;

    public String getText() {
        return text;
    }

    public String getFile() {
        return file;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("text", text).add("file", file).add("status", status).toString();
    }
}
