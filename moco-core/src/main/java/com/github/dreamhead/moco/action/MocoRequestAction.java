package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public abstract class MocoRequestAction implements MocoEventAction {
    protected final Resource url;

    protected abstract HttpRequestBase createRequest(final Resource url, final Request request);

    public MocoRequestAction(final Resource url) {
        this.url = url;
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
        client.execute(createRequest(url, request));
    }
}
