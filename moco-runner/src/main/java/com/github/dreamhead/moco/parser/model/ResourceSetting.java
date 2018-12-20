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
    private List<RestGetSetting> get;
    private List<RestPostSetting> post;
    private List<RestPutSetting> put;
    private List<RestDeleteSetting> delete;
    private List<RestHeadSetting> head;
    private List<RestPatchSetting> patch;
    private List<RestSubResourceSetting> resource;

    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("get", get)
                .add("post", post)
                .add("put", put)
                .add("delete", delete)
                .add("head", head)
                .add("patch", patch)
                .add("sub resources", resource);
    }

    @SuppressWarnings("unchecked")
    public final RestSetting[] getSettings() {
        return toArray(concat(asRestSetting(get), asRestSetting(post),
                        asRestSetting(put), asRestSetting(delete),
                        asRestSetting(head), asRestSetting(patch), asSubRestSetting(resource)),
                RestSetting.class);
    }
}
