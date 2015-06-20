package com.github.dreamhead.moco.helper;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;

public class MocoTestHelper {
    private final Executor EXECUTOR;

    public MocoTestHelper() {
        // make fluent HC accept any certificates so we can test HTTPS calls as well
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", new SSLConnectionSocketFactory(createClientContext()))
                .build();
        HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        EXECUTOR = Executor.newInstance(HttpClients.custom().setConnectionManager(cm).build());
    }

    public String get(String url) throws IOException {
        return get(Request.Get(url));
    }

    public byte[] getAsBytes(String url) throws IOException {
        return getAsBytes(Request.Get(url));
    }

    public HttpResponse getResponse(String url) throws IOException {
        return EXECUTOR.execute(Request.Get(url)).returnResponse();
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
        return EXECUTOR.execute(request).returnContent().asString();
    }

    public byte[] getAsBytes(Request request) throws IOException {
        return EXECUTOR.execute(request).returnContent().asBytes();
    }

    public String postContent(String url, String postContent) throws IOException {
        return postBytes(url, postContent.getBytes());
    }

    public String postBytes(String url, byte[] bytes) throws IOException {
        Request request = Request.Post(url)
                .addHeader(CONTENT_TYPE, PLAIN_TEXT_UTF_8.toString())
                .bodyByteArray(bytes);
        return EXECUTOR.execute(request).returnContent().asString();
    }

    public String postStream(String url, InputStream stream) throws IOException {
        return postBytes(url, toByteArray(stream));
    }

    public String postFile(String url, String file) throws IOException {
        return postStream(url, Resources.getResource(file).openStream());
    }

    public int getForStatus(String url) throws IOException {
        return EXECUTOR.execute(Request.Get(url)).returnResponse().getStatusLine().getStatusCode();
    }

    private static final String PROTOCOL = "TLS";

    private static SSLContext createClientContext() {
        try {
            SSLContext clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, AnyCertificateAcceptingTrustManagerFactory.getTrustManagers(), null);
            return clientContext;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the client-side SSLContext", e);
        }
    }
}
