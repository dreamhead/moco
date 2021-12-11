package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.MoreObjects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StructSetting {
    private Object json;

    public final boolean isJson() {
        return json != null;
    }

    public final Object getJson() {
        return json;
    }

    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this)
                .add("json", json)
                .toString();
    }
}
