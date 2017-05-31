package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import static com.google.common.base.Optional.of;

public class MocoPostRequestAction extends MocoRequestAction {
    public MocoPostRequestAction(Resource url, ContentResource content) {
        super(url, of(content));
    }

    protected HttpRequestBase createRequest(final Resource url, final Request request) {
        String targetUrl = url.readFor(of(request)).toString();
        return new HttpPost(targetUrl);
    }

    @Override
    protected MocoEventAction applyContent(MocoConfig config, ContentResource originalContent) {
        Resource appliedContent = originalContent.apply(config);
        if (appliedContent != originalContent) {
            return new MocoPostRequestAction(this.url, (ContentResource) appliedContent);
        }

        return this;

    }
}
