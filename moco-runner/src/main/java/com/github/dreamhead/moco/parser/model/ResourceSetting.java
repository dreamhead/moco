package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.collect.FluentIterable;

import java.util.List;

import static com.github.dreamhead.moco.parser.model.RestGetSetting.toGetRestSetting;
import static com.github.dreamhead.moco.parser.model.RestPostSetting.toPostSetting;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResourceSetting {
    private String name;
    private List<RestGetSetting> get;
    private List<RestPostSetting> post;

    public String getName() {
        return name;
    }

    public RestSetting[] getSettings() {
        FluentIterable<RestSetting> getSettings = FluentIterable.from(get).transform(toGetRestSetting());
        FluentIterable<RestSetting> postSettings = FluentIterable.from(post).transform(toPostSetting());

        return getSettings.append(postSettings).toArray(RestSetting.class);
    }
}
