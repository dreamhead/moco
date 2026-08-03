package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.util.ToStringHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class BaseResourceSetting {
    private TextContainer text;
    private FileContainer file;
    @JsonProperty("path_resource")
    private FileContainer pathResource;
    private Object json;

    protected ToStringHelper toStringHelper() {
        return ToStringHelper.of(this)
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
