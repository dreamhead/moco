package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.collect.FluentIterable;

import java.util.List;

import static com.github.dreamhead.moco.parser.model.RestBaseSetting.toSetting;
import static com.google.common.collect.FluentIterable.from;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResourceSetting {
    private String name;
    private List<RestGetSetting> get;
    private List<RestPostSetting> post;
    private List<RestPutSetting> put;
    private List<RestDeleteSetting> delete;
    private List<RestHeadSetting> head;
    private List<RestPatchSetting> patch;

    public String getName() {
        return name;
    }

    public RestSetting[] getSettings() {
        FluentIterable<RestSetting> getSettings = from(get).transform(toSetting());
        FluentIterable<RestSetting> postSettings = from(post).transform(toSetting());
        FluentIterable<RestSetting> putSettings = from(put).transform(toSetting());
        FluentIterable<RestSetting> deleteSettings = from(delete).transform(toSetting());
        FluentIterable<RestSetting> headSettings = from(head).transform(toSetting());
        FluentIterable<RestSetting> patchSettings = from(patch).transform(toSetting());

        return getSettings.append(postSettings)
                .append(putSettings).append(deleteSettings)
                .append(headSettings).append(patchSettings)
                .toArray(RestSetting.class);
    }
}
