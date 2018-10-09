package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.post;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class PostSetting {
    private TextContainer url;
    private TextContainer content;
    private Object json;

    public MocoEventAction createAction() {
        Optional<ContentResource> postContent = postContent();
        if (postContent.isPresent()) {
            return post(this.url.asResource(), postContent.get());
        }

        throw new IllegalArgumentException("content or json should be setup for post event");
    }

    private Optional<ContentResource> postContent() {
        if (content != null) {
            return of(content.asResource());
        }

        if (json != null) {
            return of(Moco.json(json));
        }

        return absent();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .add("content", content)
                .add("json", json)
                .toString();
    }
}
