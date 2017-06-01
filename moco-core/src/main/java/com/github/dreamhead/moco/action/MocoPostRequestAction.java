package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;

import static com.google.common.base.Optional.of;

public class MocoPostRequestAction extends MocoRequestAction {
    private final ContentResource content;

    public MocoPostRequestAction(final Resource url, final ContentResource content) {
        super(url);
        this.content = content;
    }

    protected HttpRequestBase createRequest(final String url, final Request request) {
        HttpPost targetRequest = new HttpPost(url);
        targetRequest.setEntity(asEntity(content, request));
        return targetRequest;
    }

    private HttpEntity asEntity(final ContentResource resource, final Request request) {
        return new InputStreamEntity(resource.readFor(of(request)).toInputStream());
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        Resource appliedContent = this.content.apply(config);
        if (appliedContent != this.content) {
            return new MocoPostRequestAction(this.getUrl(), (ContentResource) appliedContent);
        }

        return this;

    }
}
