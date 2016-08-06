package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoEventAction;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.post;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PostSetting {
    private TextContainer url;
    private TextContainer content;

    public MocoEventAction createAction() {
        return post(this.url.asResource(), content.asResource());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .add("content", content)
                .toString();
    }
}
