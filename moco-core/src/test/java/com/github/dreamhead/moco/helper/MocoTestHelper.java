package com.github.dreamhead.moco.helper;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.Resources;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Executor;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .build())
                .disableDefaultUserAgent()
                .build();

        executor = Executor.newInstance(client);
    }

    public String get(final String url) throws IOException {
        return executeAsString(Request.get(url));
    }

    public byte[] getAsBytes(final String url) throws IOException {
        return executor.execute(Request.get(url)).returnContent().asBytes();
    }

    public ClassicHttpResponse getResponse(final String url) throws IOException {
        return execute(Request.get(url));
    }

    public String getWithHeader(final String url, final ImmutableMultimap<String, String> headers) throws IOException {
        return executeAsString(getRequest(url, headers));
    }

    private Request getRequest(final String url, final ImmutableMultimap<String, String> headers) {
        Request request = Request.get(url);
        for (Map.Entry<String, String> entry : headers.entries()) {
            request = request.addHeader(entry.getKey(), entry.getValue());
        }
        return request;
    }

    public ClassicHttpResponse getResponseWithHeader(final String url, final ImmutableMultimap<String, String> headers)
            throws IOException {
        return execute(getRequest(url, headers));
    }

    public String getWithVersion(final String url, final HttpVersion version) throws IOException {
        return executeAsString(Request.get(url).version(version));
    }

    public String postContent(final String url, final String postContent) throws IOException {
        return postBytes(url, postContent.getBytes());
    }

    public String postBytes(final String url, final byte[] bytes) throws IOException {
        return postBytes(url, bytes, Charset.defaultCharset());
    }

    public String postBytes(final String url, final byte[] bytes, final Charset charset) throws IOException {
        return executeAsString(Request.post(url)
                .bodyByteArray(bytes, ContentType.create("text/plain", charset)));
    }

    public ClassicHttpResponse postForResponse(final String url, final String content) throws IOException {
        return postForResponse(url, content, PLAIN_TEXT_UTF_8.toString());
    }

    public ClassicHttpResponse postForResponse(final String url, final String content, final String contentType)
            throws IOException {
        return execute(Request.post(url)
                .addHeader(CONTENT_TYPE, contentType)
                .bodyByteArray(content.getBytes()));
    }

    public ClassicHttpResponse putForResponse(final String url, final String content) throws IOException {
        return execute(Request.put(url)
                .addHeader(CONTENT_TYPE, PLAIN_TEXT_UTF_8.toString())
                .bodyByteArray(content.getBytes()));
    }

    public ClassicHttpResponse putForResponseWithHeaders(final String url, final String content,
                                                  final ImmutableMultimap<String, String> headers) throws IOException {
        Request request = Request.put(url)
                .bodyByteArray(content.getBytes());
        for (Map.Entry<String, String> entry : headers.entries()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
        return execute(request);
    }

    public ClassicHttpResponse deleteForResponse(final String url) throws IOException {
        return execute(Request.delete(url));
    }

    public ClassicHttpResponse deleteForResponseWithHeaders(final String url, final ImmutableMultimap<String, String> headers)
            throws IOException {
        Request request = Request.delete(url);
        for (Map.Entry<String, String> entry : headers.entries()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
        return execute(request);
    }

    public ClassicHttpResponse headForResponse(final String url) throws IOException {
        return execute(Request.head(url));
    }

    public String postStream(final String url, final InputStream stream) throws IOException {
        return postBytes(url, toByteArray(stream));
    }

    public String postFile(final String url, final String file) throws IOException {
        return postStream(url, Resources.getResource(file).openStream());
    }

    public int getForStatus(final String url) throws IOException {
        return execute(Request.get(url)).getCode();
    }

    public String patchForResponse(final String url, final String content) throws IOException {
        return executeAsString(Request.patch(url)
                .bodyString(content, ContentType.DEFAULT_TEXT));
    }

    public ClassicHttpResponse execute(final Request request) throws IOException {
        return (ClassicHttpResponse) executor.execute(request).returnResponse();
    }

    public String executeAsString(final Request request) throws IOException {
        final Response response = executor.execute(request);
        final Content content = response.returnContent();
        if (content.asBytes().length <= 0) {
            return null;
        }

        return content.asString();
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
