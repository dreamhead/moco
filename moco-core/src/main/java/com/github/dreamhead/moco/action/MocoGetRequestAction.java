package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import static com.google.common.base.Optional.of;

public class MocoGetRequestAction extends MocoRequestAction {
    public MocoGetRequestAction(Resource url) {
        super(url, Optional.<ContentResource>absent());
    }

    protected HttpRequestBase createRequest(final Resource url, final Request request) {
        String targetUrl = url.readFor(of(request)).toString();
        return new HttpGet(targetUrl);
    }

    @Override
    protected MocoEventAction applyContent(MocoConfig config, ContentResource originalContent) {
        Resource appliedContent = originalContent.apply(config);
        if (appliedContent != originalContent) {
            return new MocoGetRequestAction(this.url);
        }

        return this;
    }
}
