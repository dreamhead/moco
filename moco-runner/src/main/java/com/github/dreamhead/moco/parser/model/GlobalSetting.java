package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.util.Globs;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import static com.github.dreamhead.moco.util.Files.join;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class GlobalSetting {
    private String include;
    private String context;
    @JsonProperty("file_root")
    private String fileRoot;
    private String env;
    private RequestSetting request;
    private ResponseSetting response;

    public ImmutableList<String> includes() {
        return Globs.glob(join(fileRoot, include));
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
