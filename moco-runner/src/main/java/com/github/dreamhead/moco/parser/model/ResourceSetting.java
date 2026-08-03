package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.util.ToStringHelper;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.dreamhead.moco.parser.model.RestBaseSetting.asRestSetting;
import static com.github.dreamhead.moco.parser.model.RestSubResourceSetting.asSubRestSetting;

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

    protected ToStringHelper toStringHelper() {
        return ToStringHelper.of(this)
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
        return Stream.of(asRestSetting(get), asRestSetting(post),
                        asRestSetting(put), asRestSetting(delete),
                        asRestSetting(head), asRestSetting(patch), asSubRestSetting(resource))
                .flatMap(settings -> StreamSupport.stream(settings.spliterator(), false))
                .toArray(RestSetting[]::new);
    }
}
