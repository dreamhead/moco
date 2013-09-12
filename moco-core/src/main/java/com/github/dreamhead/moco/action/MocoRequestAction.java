package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import static java.lang.String.format;

public class MocoRequestAction implements MocoEventAction {
    private final String url;
    private final String method;

    public MocoRequestAction(String url, String method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public void execute() {
        HttpClient client = new DefaultHttpClient();
        try {
            client.execute(createRequest(url, method));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequestBase createRequest(String url, String method) {
        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            return new HttpGet(url);
        }

        throw new RuntimeException(format("unknown HTTP method: %s", method));
    }
}
