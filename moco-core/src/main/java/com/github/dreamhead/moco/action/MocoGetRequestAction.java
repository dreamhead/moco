package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public final class MocoGetRequestAction extends MocoRequestAction {
    public MocoGetRequestAction(final Resource url, final HttpHeader[] headers) {
        super(url, headers);
    }

    protected HttpRequestBase createRequest(final String url, final Request request) {
        return new HttpGet(url);
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        HttpHeader[] headers = applyHeaders(config);
        if (sameHeaders(headers)) {
            return this;
        }

        return new MocoGetRequestAction(this.getUrl(), headers);
    }
}
