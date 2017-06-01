package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static com.google.common.base.Optional.of;

public abstract class MocoRequestAction implements MocoEventAction {
    private final Resource url;

    protected abstract HttpRequestBase createRequest(final String url, final Request request);

    protected MocoRequestAction(final Resource url) {
        this.url = url;
    }

    public Resource getUrl() {
        return url;
    }

    @Override
    public void execute(final Request request) {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            doExecute(client, request);
        } catch (IOException e) {
            throw new MocoException(e);
        } finally {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void doExecute(final CloseableHttpClient client, final Request request) throws IOException {
        String targetUrl = url.readFor(of(request)).toString();
        client.execute(createRequest(targetUrl, request));
    }
}
