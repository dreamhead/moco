package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.util.Globs;
import com.github.dreamhead.moco.util.ToStringHelper;

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

    public List<String> includes() {
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
        return ToStringHelper.of(this)
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
