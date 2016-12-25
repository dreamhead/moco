package com.github.dreamhead.moco.helper;

import com.google.common.collect.ImmutableMultimap;
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
import org.apache.http.entity.ContentType;
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
    private final Executor executor;

    public MocoTestHelper() {
        // make fluent HC accept any certificates so we can test HTTPS calls as well
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", new SSLConnectionSocketFactory(createClientContext()))
                .build();
        HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        executor = Executor.newInstance(HttpClients.custom().setConnectionManager(cm).build());
    }

    public String get(final String url) throws IOException {
        return get(Request.Get(url));
    }

    public byte[] getAsBytes(final String url) throws IOException {
        return getAsBytes(Request.Get(url));
    }

    public HttpResponse getResponse(final String url) throws IOException {
        Request request = Request.Get(url);
        return runRequest(request);
    }

    public String getWithHeader(final String url, final ImmutableMultimap<String, String> headers) throws IOException {
        Request request = getRequest(url, headers);

        return get(request);
    }

    private Request getRequest(final String url, final ImmutableMultimap<String, String> headers) {
        Request request = Request.Get(url);
        for (Map.Entry<String, String> entry : headers.entries()) {
            request = request.addHeader(entry.getKey(), entry.getValue());
        }
        return request;
    }

    public HttpResponse getResponseWithHeader(final String url, final ImmutableMultimap<String, String> headers)
            throws IOException {
        return runRequest(getRequest(url, headers));
    }

    private HttpResponse runRequest(final Request request) throws IOException {
        return executor.execute(request).returnResponse();
    }

    public String getWithVersion(final String url, final HttpVersion version) throws IOException {
        return get(Request.Get(url).version(version));
    }

    private String get(final Request request) throws IOException {
        return executor.execute(request).returnContent().asString();
    }

    public byte[] getAsBytes(final Request request) throws IOException {
        return executor.execute(request).returnContent().asBytes();
    }

    public String postContent(final String url, final String postContent) throws IOException {
        return postBytes(url, postContent.getBytes());
    }

    public String postBytes(final String url, final byte[] bytes) throws IOException {
        Request request = Request.Post(url)
                .addHeader(CONTENT_TYPE, PLAIN_TEXT_UTF_8.toString())
                .bodyByteArray(bytes);
        return executor.execute(request).returnContent().asString();
    }

    public HttpResponse postForResponse(final String url, final String content) throws IOException {
        return postForResponse(url, content, PLAIN_TEXT_UTF_8.toString());
    }

    public HttpResponse postForResponse(final String url, final String content, final String contentType)
            throws IOException {
        Request request = Request.Post(url)
                .addHeader(CONTENT_TYPE, contentType)
                .bodyByteArray(content.getBytes());
        return executor.execute(request).returnResponse();
    }

    public HttpResponse putForResponse(final String url, final String content) throws IOException {
        Request request = Request.Put(url)
                .addHeader(CONTENT_TYPE, PLAIN_TEXT_UTF_8.toString())
                .bodyByteArray(content.getBytes());
        return executor.execute(request).returnResponse();
    }

    public HttpResponse putForResponseWithHeaders(final String url, final String content,
                                                  final ImmutableMultimap<String, String> headers) throws IOException {
        Request request = Request.Put(url)
                .bodyByteArray(content.getBytes());
        for (Map.Entry<String, String> entry : headers.entries()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
        return executor.execute(request).returnResponse();
    }

    public HttpResponse deleteForResponse(final String url) throws IOException {
        Request request = Request.Delete(url);
        return executor.execute(request).returnResponse();
    }

    public HttpResponse deleteForResponseWithHeaders(final String url, final ImmutableMultimap<String, String> headers)
            throws IOException {
        Request request = Request.Delete(url);
        for (Map.Entry<String, String> entry : headers.entries()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
        return executor.execute(request).returnResponse();
    }

    public HttpResponse headForResponse(final String url) throws IOException {
        Request request = Request.Head(url);
        return executor.execute(request).returnResponse();
    }

    public String postStream(final String url, final InputStream stream) throws IOException {
        return postBytes(url, toByteArray(stream));
    }

    public String postFile(final String url, final String file) throws IOException {
        return postStream(url, Resources.getResource(file).openStream());
    }

    public int getForStatus(final String url) throws IOException {
        return runRequest(Request.Get(url)).getStatusLine().getStatusCode();
    }

    public String patchForResponse(final String url, final String content) throws IOException {
        Request request = Request.Patch(url).bodyByteArray(content.getBytes(), ContentType.DEFAULT_TEXT);
        return executor.execute(request).returnContent().asString();
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
