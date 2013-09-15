package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.resource.ContentResource;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static java.lang.String.format;

public class MocoRequestAction implements MocoEventAction {
    private final String url;
    private final String method;
    private final ContentResource content;

    public MocoRequestAction(String url, String method, ContentResource content) {
        this.url = url;
        this.method = method;
        this.content = content;
    }

    @Override
    public void execute() {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpRequestBase request = createRequest(url, method);
            if (request instanceof HttpEntityEnclosingRequest) {
                ((HttpEntityEnclosingRequest)request).setEntity(new ByteArrayEntity(content.readFor(null)));
            }

            client.execute(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
