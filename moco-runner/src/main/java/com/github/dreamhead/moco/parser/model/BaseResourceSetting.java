package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public abstract class BaseResourceSetting {
    private TextContainer text;
    private FileContainer file;
    @JsonProperty("path_resource")
    private FileContainer pathResource;
    private Object json;

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("file", file)
                .add("path resource", pathResource)
                .add("json", json);
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    protected final <T extends BaseResourceSetting> T asBaseResourceSetting(final T setting) {
        BaseResourceSetting base = setting;
        base.text = text;
        base.file = file;
        base.pathResource = pathResource;
        base.json = json;
        return setting;
    }
}
