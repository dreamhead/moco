package com.github.dreamhead.moco.helper;

import com.google.common.io.Resources;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;

public class MocoTestHelper {
    public String get(String uri) throws IOException {
        Content content = Request.Get(uri).execute().returnContent();
        return content.asString();
    }

    public String postContent(String uri, String postContent) throws IOException {
        return postBytes(uri, postContent.getBytes());
    }

    public String postBytes(String uri, byte[] bytes) throws IOException {
        Content content = Request.Post(uri).bodyByteArray(bytes)
                .execute().returnContent();
        return content.asString();
    }

    public String postFile(String uri, String file) throws IOException {
        InputStream is = Resources.getResource(file).openStream();
        Content content = Request.Post(uri).bodyByteArray(toByteArray(is))
                .execute().returnContent();
        return content.asString();
    }
}
