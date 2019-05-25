package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;

public class HttpHeaderResponseHandler extends AbstractHttpResponseHandler {
    private HttpHeader header;

    public HttpHeaderResponseHandler(final HttpHeader header) {
        this.header = header;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        String value = header.getValue().readFor(httpRequest).toString();
        httpResponse.addHeader(header.getName(), value);
    }

    @Override
    public ResponseHandler apply(MocoConfig config) {
        Resource value = this.header.getValue();
        Resource appliedResource = value.apply(config);
        if (appliedResource != value) {
            return new HttpHeaderResponseHandler(new HttpHeader(this.header.getName(), appliedResource));
        }

        return super.apply(config);
    }
}
