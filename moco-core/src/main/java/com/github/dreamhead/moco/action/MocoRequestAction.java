package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class MocoRequestAction implements MocoEventAction {
    private final ActionMonitor monitor = new ActionMonitor();

    private final Resource url;
    private final HttpHeader[] headers;

    protected abstract ClassicHttpRequest createRequest(String url, Request request);

    protected MocoRequestAction(final Resource url, final HttpHeader[] headers) {
        this.url = url;
        this.headers = headers;
    }

    @Override
    public final void execute(final Request request) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            final ClassicHttpRequest actual = prepareRequest(request);
            monitor.preAction(actual);
            final ClassicHttpResponse response = client.execute(actual, resp -> resp);
            monitor.postAction(response);
        } catch (IOException | URISyntaxException e) {
            throw new MocoException(e);
        }
    }

    private ClassicHttpRequest prepareRequest(final Request request) {
        ClassicHttpRequest httpRequest = createRequest(url.readFor(request).toString(), request);
        for (HttpHeader header : headers) {
            httpRequest.addHeader(header.getName(), header.getValue().readFor(request).toString());
        }

        return httpRequest;
    }

    protected final Resource applyUrl(final MocoConfig config) {
        return this.url.apply(config);
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
