package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public abstract class BaseResourceSetting {
    protected TextContainer text;
    protected FileContainer file;
    @JsonProperty("path_resource")
    protected FileContainer pathResource;
    protected Object json;

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("file", file)
                .add("path resource", pathResource)
                .add("json", json);
    }
}
