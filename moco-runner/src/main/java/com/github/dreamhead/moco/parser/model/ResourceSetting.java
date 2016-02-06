package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.collect.FluentIterable;

import java.util.List;

import static com.github.dreamhead.moco.parser.model.RestDeleteSetting.toDeleteSetting;
import static com.github.dreamhead.moco.parser.model.RestGetSetting.toGetSetting;
import static com.github.dreamhead.moco.parser.model.RestHeadSetting.toHeadSetting;
import static com.github.dreamhead.moco.parser.model.RestPatchSetting.toPatchSetting;
import static com.github.dreamhead.moco.parser.model.RestPostSetting.toPostSetting;
import static com.github.dreamhead.moco.parser.model.RestPutSetting.toPutSetting;

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
        FluentIterable<RestSetting> getSettings = FluentIterable.from(get).transform(toGetSetting());
        FluentIterable<RestSetting> postSettings = FluentIterable.from(post).transform(toPostSetting());
        FluentIterable<RestSetting> putSettings = FluentIterable.from(put).transform(toPutSetting());
        FluentIterable<RestSetting> deleteSettings = FluentIterable.from(delete).transform(toDeleteSetting());
        FluentIterable<RestSetting> headSettings = FluentIterable.from(head).transform(toHeadSetting());
        FluentIterable<RestSetting> patchSettings = FluentIterable.from(patch).transform(toPatchSetting());

        return getSettings.append(postSettings)
                .append(putSettings).append(deleteSettings)
                .append(headSettings).append(patchSettings)
                .toArray(RestSetting.class);
    }
}
