package com.github.dreamhead.moco.helper;

import com.github.dreamhead.moco.internal.HttpsCertificate;
import com.github.dreamhead.moco.internal.MocoSslContextFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.github.dreamhead.moco.internal.HttpsCertificate.classpathCertificate;
import static com.google.common.io.ByteStreams.toByteArray;

public class MocoTestHelper {

    public static final HttpsCertificate CERTIFICATE = classpathCertificate("/cert.jks", "mocohttps", "mocohttps");

    private static final Executor EXECUTOR;

    static {
        // make fluent HC accept any certificates so we can test HTTPS calls as well
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", new SSLConnectionSocketFactory(MocoSslContextFactory.createClientContext()))
                .build();
        HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        EXECUTOR = Executor.newInstance(HttpClients.custom().setConnectionManager(cm).build());
    }

    public String get(String url) throws IOException {
        return get(Request.Get(url));
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

    public String postContent(String url, String postContent) throws IOException {
        return postBytes(url, postContent.getBytes());
    }

    public String postBytes(String url, byte[] bytes) throws IOException {
        return EXECUTOR.execute(Request.Post(url).bodyByteArray(bytes)).returnContent().asString();
    }

    public String postFile(String url, String file) throws IOException {
        InputStream is = Resources.getResource(file).openStream();
        return EXECUTOR.execute(Request.Post(url).bodyByteArray(toByteArray(is))).returnContent().asString();
    }

    public int getForStatus(String url) throws IOException {
        return EXECUTOR.execute(Request.Get(url)).returnResponse().getStatusLine().getStatusCode();
    }
}
