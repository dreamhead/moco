package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.util.Files.join;

public class GlobalSetting {
    private String include;
    private String context;
    @JsonProperty("file_root")
    private String fileRoot;
    private String env;
    private RequestSetting request;
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

    public RequestSetting getRequest() {
        return request;
    }

    public ResponseSetting getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("include", include)
                .add("context", context)
                .add("file root", fileRoot)
                .add("env", env)
                .add("request", request)
                .add("response", response)
                .toString();
    }
}
