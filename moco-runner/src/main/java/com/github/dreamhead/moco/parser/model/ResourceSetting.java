package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.collect.FluentIterable;

import java.util.List;

import static com.github.dreamhead.moco.parser.model.RestGetSetting.toGetRestSetting;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResourceSetting {
    private String name;
    private List<RestGetSetting> get;

    public String getName() {
        return name;
    }

    public RestSetting[] getSettings() {
        return FluentIterable.from(get).transform(toGetRestSetting()).toArray(RestSetting.class);
    }
}
