package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.util.ToStringHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StructSetting {
    private Object json;
    private String xml;

    public final boolean isJson() {
        return json != null;
    }

    public final Object getJson() {
        return json;
    }

    public final boolean isXml() {
        return xml != null;
    }

    public final String getXml() {
        return this.xml;
    }

    @Override
    public final String toString() {
        return ToStringHelper.of(this)
                .add("json", json)
                .add("xml", xml)
                .toString();
    }
}
