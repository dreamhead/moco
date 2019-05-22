package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;

public final class MocoPostRequestAction extends MocoRequestAction {
    private final ContentResource content;

    public MocoPostRequestAction(final Resource url, final ContentResource content,
                                 final ImmutableMap<String, Resource> headers) {
        super(url, headers);
        this.content = content;
    }

    protected HttpRequestBase createRequest(final String url, final Request request) {
        HttpPost targetRequest = new HttpPost(url);
        targetRequest.setEntity(asEntity(content, request));
        return targetRequest;
    }

    private HttpEntity asEntity(final ContentResource resource, final Request request) {
        return new ByteArrayEntity(resource.readFor(request).getContent(), getContentType((HttpRequest) request));
    }

    private ContentType getContentType(final HttpRequest request) {
        MediaType type = content.getContentType(request);
        return ContentType.create(type.type() + "/" + type.subtype(),
                type.charset().or(Charset.defaultCharset()));
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        Resource appliedContent = this.content.apply(config);
        if (appliedContent != this.content) {
            return new MocoPostRequestAction(this.getUrl(), (ContentResource) appliedContent, ImmutableMap.<String, Resource>of());
        }

        return this;
    }
}
