package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.collect.FluentIterable;

import java.util.List;

import static com.github.dreamhead.moco.parser.model.RestGetSetting.toGetRestSetting;
import static com.github.dreamhead.moco.parser.model.RestPostSetting.toPostSetting;
import static com.github.dreamhead.moco.parser.model.RestPutSetting.toPutSetting;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResourceSetting {
    private String name;
    private List<RestGetSetting> get;
    private List<RestPostSetting> post;
    private List<RestPutSetting> put;

    public String getName() {
        return name;
    }

    public RestSetting[] getSettings() {
        FluentIterable<RestSetting> getSettings = FluentIterable.from(get).transform(toGetRestSetting());
        FluentIterable<RestSetting> postSettings = FluentIterable.from(post).transform(toPostSetting());
        FluentIterable<RestSetting> putSettings = FluentIterable.from(put).transform(toPutSetting());

        return getSettings.append(postSettings).append(putSettings).toArray(RestSetting.class);
    }
}
