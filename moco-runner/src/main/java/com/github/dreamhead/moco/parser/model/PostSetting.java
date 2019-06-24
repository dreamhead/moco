package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.util.Iterables;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.post;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class PostSetting extends BaseActionSetting {
    private TextContainer url;
    private Map<String, TextContainer> headers;
    private TextContainer content;
    private Object json;

    public MocoEventAction createAction() {
        Optional<ContentResource> postContent = postContent();
        if (postContent.isPresent()) {
            ContentResource content = postContent.get();
            return doCreateAction(content);
        }

        throw new IllegalArgumentException("content or json should be setup for post event");
    }

    private MocoEventAction doCreateAction(final ContentResource content) {
        ContentResource url = this.url.asResource();

        if (headers == null) {
            return post(url, content);
        }

        HttpHeader[] headers = asHeaders(this.headers.entrySet());
        return post(url, content, Iterables.head(headers), Iterables.tail(headers));
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
                .add("headers", headers)
                .add("content", content)
                .add("json", json)
                .toString();
    }
}
