package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.MoreObjects;

import java.util.List;

import static com.github.dreamhead.moco.parser.model.RestBaseSetting.asRestSetting;
import static com.github.dreamhead.moco.parser.model.RestSubResourceSetting.asSubRestSetting;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResourceSetting {
    private String name;
    protected List<RestGetSetting> get;
    protected List<RestPostSetting> post;
    protected List<RestPutSetting> put;
    protected List<RestDeleteSetting> delete;
    protected List<RestHeadSetting> head;
    protected List<RestPatchSetting> patch;
    protected List<RestSubResourceSetting> resource;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("get", get)
                .add("post", post)
                .add("put", put)
                .add("delete", delete)
                .add("head", head)
                .add("patch", patch)
                .add("sub resource", resource)
                .toString();
    }

    @SuppressWarnings("unchecked")
    public RestSetting[] getSettings() {
        return toArray(concat(asRestSetting(get), asRestSetting(post),
                        asRestSetting(put), asRestSetting(delete),
                        asRestSetting(head), asRestSetting(patch), asSubRestSetting(resource)),
                RestSetting.class);
    }
}
