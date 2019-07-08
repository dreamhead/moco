package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public abstract class MocoRequestAction implements MocoEventAction {
    private final Resource url;
    private final HttpHeader[] headers;

    protected abstract HttpRequestBase createRequest(String url, Request request);

    protected MocoRequestAction(final Resource url, final HttpHeader[] headers) {
        this.url = url;
        this.headers = headers;
    }

    protected final Resource getUrl() {
        return url;
    }

    @Override
    public final void execute(final Request request) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            doExecute(client, request);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    private void doExecute(final CloseableHttpClient client, final Request request) throws IOException {
        String targetUrl = url.readFor(request).toString();
        HttpRequestBase httpRequest = createRequest(targetUrl, request);
        for (HttpHeader header : headers) {
            httpRequest.setHeader(header.getName(), header.getValue().readFor(null).toString());
        }

        client.execute(httpRequest);
    }

    protected HttpHeader[] applyHeaders(MocoConfig config) {
        HttpHeader[] appliedHeaders = new HttpHeader[this.headers.length];
        for (int i = 0; i < headers.length; i++) {
            HttpHeader header = headers[i];
            Resource appliedValue = header.getValue().apply(config);
            appliedHeaders[i] = new HttpHeader(header.getName(), appliedValue);
        }

        return appliedHeaders;
    }
}
