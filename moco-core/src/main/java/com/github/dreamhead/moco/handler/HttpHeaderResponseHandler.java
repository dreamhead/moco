package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.ResponseHandler;

public class HttpHeaderResponseHandler extends AbstractHttpResponseHandler {
    private HttpHeader header;

    public HttpHeaderResponseHandler(final HttpHeader header) {
        this.header = header;
    }

    @Override
    protected final void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        String value = header.getValue().readFor(httpRequest).toString();
        httpResponse.addHeader(header.getName(), value);
    }

    @Override
    public final ResponseHandler doApply(final MocoConfig config) {
        HttpHeader appliedHeader = this.header.apply(config);
        if (appliedHeader != this.header) {
            return new HttpHeaderResponseHandler(appliedHeader);
        }

        return this;
    }
}
