package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.MoreObjects;

import java.util.Map;
import java.util.Optional;

import static com.github.dreamhead.moco.Moco.post;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class PostSetting extends BaseActionSetting {
    private TextContainer url;
    private Map<String, TextContainer> headers;
    private TextContainer content;
    private Object json;

    public MocoEventAction createAction() {
        Optional<ContentResource> postContent = postContent();
        final ContentResource content = postContent.orElseThrow(() ->
                new IllegalArgumentException("content or json should be setup for post event"));
        return doCreateAction(content);
    }

    private MocoEventAction doCreateAction(final ContentResource content) {
        ContentResource url = this.url.asResource();
        return post(url, content, asHeaders(this.headers));
    }

    private Optional<ContentResource> postContent() {
        if (content != null) {
            return of(content.asResource());
        }

        if (json != null) {
            return of(Moco.json(json));
        }

        return empty();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .add("headers", headers)
                .add("content", content)
                .add("json", json)
                .toString();
    }
}
