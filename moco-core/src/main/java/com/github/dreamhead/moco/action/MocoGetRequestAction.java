package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public final class MocoGetRequestAction extends MocoRequestAction {
    public MocoGetRequestAction(final Resource url, final HttpHeader[] headers) {
        super(url, headers);
    }

    protected HttpUriRequest createRequest(final String url, final Request request) {
        return new HttpGet(url);
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        Resource appliedUrl = applyUrl(config);
        HttpHeader[] headers = applyHeaders(config);
        if (isSameUrl(appliedUrl) && isSameHeaders(headers)) {
            return this;
        }

        return new MocoGetRequestAction(appliedUrl, headers);
    }
}
