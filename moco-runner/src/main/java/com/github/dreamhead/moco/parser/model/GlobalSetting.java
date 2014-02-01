package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.github.dreamhead.moco.util.Files.join;

public class GlobalSetting {
    private String include;
    private String context;
    @JsonProperty("file_root")
    private String fileRoot;
    private String env;
    private ResponseSetting response;

    public String getInclude() {
        return join(fileRoot, include);
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

    public ResponseSetting getResponse() {
        return response;
    }
}
