package com.github.dreamhead.moco.helper;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.google.common.io.ByteStreams.toByteArray;

public class MocoTestHelper {
    public String get(String url) throws IOException {
        return get(Request.Get(url));
    }

    public HttpResponse getResponse(String url) throws IOException {
        return Request.Get(url).execute().returnResponse();
    }

    public String getWithHeader(String url, ImmutableMap<String, String> headers) throws IOException {
        Request request = Request.Get(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request = request.addHeader(entry.getKey(), entry.getValue());
        }

        return get(request);
    }

    public String getWithVersion(String url, HttpVersion version) throws IOException {
        return get(Request.Get(url).version(version));
    }

    private String get(Request request) throws IOException {
        return request.execute().returnContent().asString();
    }

    public String postContent(String url, String postContent) throws IOException {
        return postBytes(url, postContent.getBytes());
    }

    public String postBytes(String url, byte[] bytes) throws IOException {
        Content content = Request.Post(url).bodyByteArray(bytes)
                .execute().returnContent();
        return content.asString();
    }

    public String postFile(String url, String file) throws IOException {
        InputStream is = Resources.getResource(file).openStream();
        Content content = Request.Post(url).bodyByteArray(toByteArray(is))
                .execute().returnContent();
        return content.asString();
    }

    public int getForStatus(String url) throws IOException {
        StatusLine statusLine = Request.Get(url).execute().returnResponse().getStatusLine();
        return statusLine.getStatusCode();
    }
}
