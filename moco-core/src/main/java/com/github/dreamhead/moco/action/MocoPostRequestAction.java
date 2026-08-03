package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.MediaType;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.net.http.HttpRequest;

public final class MocoPostRequestAction extends MocoRequestAction {
    private final ContentResource content;

    public MocoPostRequestAction(final Resource url, final ContentResource content,
                                 final HttpHeader[] headers) {
        super(url, headers);
        this.content = content;
    }

    @Override
    protected HttpRequest.Builder createRequestBuilder(final String url, final Request request) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofByteArray(content.readFor(request).getContent()));

        builder.header("Content-Type", getContentType((com.github.dreamhead.moco.HttpRequest) request));

        return builder;
    }

    private @NonNull String getContentType(final com.github.dreamhead.moco.HttpRequest request) {
        MediaType type = content.getContentType(request);
        String contentTypeValue = type.type() + "/" + type.subtype();
        if (type.charset().isPresent()) {
            contentTypeValue = contentTypeValue + "; charset=" + type.charset().get();
        }
        return contentTypeValue;
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        Resource appliedUrl = this.applyUrl(config);
        Resource appliedContent = this.content.apply(config);
        HttpHeader[] headers = applyHeaders(config);
        if (isSameUrl(appliedUrl) && appliedContent == this.content && isSameHeaders(headers)) {
            return this;
        }

        return new MocoPostRequestAction(appliedUrl, (ContentResource) appliedContent, headers);
    }
}
