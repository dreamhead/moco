package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalSetting {
    private String include;
    private String context;
    @JsonProperty("file_root")
    private String fileRoot;

    public String getInclude() {
        return include;
    }

    public String getContext() {
        return context;
    }

    public String getFileRoot() {
        return fileRoot;
    }
}
