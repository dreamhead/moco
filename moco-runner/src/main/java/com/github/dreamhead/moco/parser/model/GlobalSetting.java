package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class GlobalSetting {
    private String include;
    private String context;
    @JsonProperty("file_root")
    private String fileRoot;
    private String env;

    public String getInclude() {
        return fileRoot != null ? new File(fileRoot, include).getPath() : include;
    }

    public String getContext() {
        return context;
    }

    public String getFileRoot() {
        return fileRoot;
    }

    public String getEnv() {
        return env;
    }
}
