package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static com.google.common.base.Optional.of;
import static java.lang.String.format;

public class MocoRequestAction implements MocoEventAction {
    private final String url;
    private final HttpMethod method;
    private final Optional<ContentResource> content;

    public MocoRequestAction(final String url, final HttpMethod method, final Optional<ContentResource> content) {
        this.url = url;
        this.method = method;
        this.content = content;
    }

    @Override
    public void execute() {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            doExecute(client);
        } catch (IOException e) {
            throw new MocoException(e);
        } finally {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void doExecute(final CloseableHttpClient client) throws IOException {
        HttpRequestBase request = createRequest(url, method);
        if (request instanceof HttpEntityEnclosingRequest && content.isPresent()) {
            ((HttpEntityEnclosingRequest) request).setEntity(asEntity(content.get()));
        }

        client.execute(request);
    }

    private HttpEntity asEntity(ContentResource resource) {
        return new InputStreamEntity(resource.readFor(Optional.<Request>absent()).toInputStream());
    }

    private HttpRequestBase createRequest(final String url, final HttpMethod method) {
        if (HttpMethod.GET == method) {
            return new HttpGet(url);
        }

        if (HttpMethod.POST == method) {
            return new HttpPost(url);
        }

        throw new MocoException(format("unknown HTTP method: %s", method));
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        if (this.content.isPresent()) {
            return applyContent(config, this.content.get());
        }

        return this;
    }

    private MocoEventAction applyContent(final MocoConfig config, final ContentResource originalContent) {
        Resource appliedContent = originalContent.apply(config);
        if (appliedContent != originalContent) {
            return new MocoRequestAction(this.url, this.method, of((ContentResource) appliedContent));
        }

        return this;
    }
}
