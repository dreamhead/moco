package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.net.MediaType;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;

import java.nio.charset.Charset;

public final class MocoPostRequestAction extends MocoRequestAction {
    private final ContentResource content;

    public MocoPostRequestAction(final Resource url, final ContentResource content,
                                 final HttpHeader[] headers) {
        super(url, headers);
        this.content = content;
    }

    protected ClassicHttpRequest createRequest(final String url, final Request request) {
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
        Resource appliedUrl = this.applyUrl(config);
        Resource appliedContent = this.content.apply(config);
        HttpHeader[] headers = applyHeaders(config);
        if (isSameUrl(appliedUrl) && appliedContent == this.content && isSameHeaders(headers)) {
            return this;
        }

        return new MocoPostRequestAction(appliedUrl, (ContentResource) appliedContent, headers);
    }
}
