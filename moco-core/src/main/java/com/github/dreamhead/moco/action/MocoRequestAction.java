package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ImmutableMap;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;

public abstract class MocoRequestAction implements MocoEventAction {
    private final Resource url;
    private final Map<String, Resource> headers;

    protected abstract HttpRequestBase createRequest(String url, Request request);

    protected MocoRequestAction(final Resource url, final ImmutableMap<String, Resource> headers) {
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
        for (Map.Entry<String, Resource> entry : headers.entrySet()) {
            httpRequest.setHeader(entry.getKey(), entry.getValue().readFor(null).toString());
        }

        client.execute(httpRequest);
    }
}
