package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static com.google.common.base.Optional.of;
import static java.lang.String.format;

public class MocoRequestAction implements MocoEventAction {
    private final String url;
    private final String method;
    private final Optional<ContentResource> content;

    public MocoRequestAction(final String url, final String method, final Optional<ContentResource> content) {
        this.url = url;
        this.method = method;
        this.content = content;
    }

    @Override
    public void execute() {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpRequestBase request = createRequest(url, method);
            if (request instanceof HttpEntityEnclosingRequest && content.isPresent()) {
                ((HttpEntityEnclosingRequest)request).setEntity(new ByteArrayEntity(content.get().readFor(Optional.<com.github.dreamhead.moco.HttpRequest>absent())));
            }

            client.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }

    private HttpRequestBase createRequest(String url, String method) {
        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            return new HttpGet(url);
        }

        if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            return new HttpPost(url);
        }

        throw new RuntimeException(format("unknown HTTP method: %s", method));
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        if (this.content.isPresent()) {
            return applyContent(config, this.content.get());
        }

        return this;
    }

    private MocoEventAction applyContent(final MocoConfig config, final ContentResource originalContent) {
        Resource content = originalContent.apply(config);
        if (content != originalContent) {
            return new MocoRequestAction(this.url, this.method, of((ContentResource) content));
        }

        return this;
    }
}
