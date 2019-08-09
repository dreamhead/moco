package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public abstract class MocoRequestAction implements MocoEventAction {
    private final Resource url;
    private final HttpHeader[] headers;

    protected abstract HttpUriRequest createRequest(String url, Request request);

    protected MocoRequestAction(final Resource url, final HttpHeader[] headers) {
        this.url = url;
        this.headers = headers;
    }

    @Override
    public final void execute(final Request request) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            client.execute(prepareRequest(request));
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    private HttpUriRequest prepareRequest(final Request request) {
        HttpUriRequest httpRequest = createRequest(url.readFor(request).toString(), request);
        for (HttpHeader header : headers) {
            httpRequest.addHeader(header.getName(), header.getValue().readFor(request).toString());
        }

        return httpRequest;
    }

    protected final Resource applyUrl(final MocoConfig config) {
        Resource appliedResource = this.url.apply(config);
        if (appliedResource == this.url) {
            return this.url;
        }

        return appliedResource;
    }

    protected final boolean isSameUrl(final Resource url) {
        return this.url == url;
    }

    protected final HttpHeader[] applyHeaders(final MocoConfig config) {
        HttpHeader[] appliedHeaders = new HttpHeader[this.headers.length];
        boolean applied = false;
        for (int i = 0; i < headers.length; i++) {
            HttpHeader appliedHeader = headers[i].apply(config);
            if (!headers[i].equals(appliedHeader)) {
                applied = true;
            }
            appliedHeaders[i] = appliedHeader;
        }

        if (applied) {
            return appliedHeaders;
        }

        return this.headers;
    }

    protected final boolean isSameHeaders(final HttpHeader[] headers) {
        return this.headers == headers;
    }
}
