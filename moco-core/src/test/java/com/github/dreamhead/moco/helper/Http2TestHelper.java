package com.github.dreamhead.moco.helper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Test helper for making HTTP/2 requests in integration tests.
 * Uses Java HttpClient which has native HTTP/2 support.
 */
public class Http2TestHelper implements AutoCloseable {
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    private final HttpClient client;
    private final boolean useHttps;

    public Http2TestHelper() {
        this(false);
    }

    public Http2TestHelper(final boolean useHttps) {
        this.useHttps = useHttps;
        this.client = createHttpClient();
    }

    private HttpClient createHttpClient() {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));

        if (useHttps) {
            builder.sslContext(createTrustAllSslContext());
        }

        return builder.build();
    }

    private SSLContext createTrustAllSslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            }, null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create SSL context", e);
        }
    }

    /**
     * Sends an HTTP/2 GET request and returns the response content.
     */
    public String get(final String url) throws IOException {
        try {
            HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    /**
     * Sends an HTTP/2 GET request and returns the full response.
     */
    public HttpResponse<String> getResponse(final String url) throws IOException {
        try {
            return client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    /**
     * Sends an HTTP/2 POST request with content.
     */
    public String post(final String url, final String content) throws IOException {
        try {
            HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .POST(HttpRequest.BodyPublishers.ofString(content))
                            .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    /**
     * Sends an HTTP/2 POST request with content and returns the full response.
     */
    public HttpResponse<String> postResponse(final String url, final String content) throws IOException {
        try {
            return client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .POST(HttpRequest.BodyPublishers.ofString(content))
                            .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    /**
     * Sends an HTTP/2 POST request with content and custom headers.
     */
    public String post(final String url, final String content, final List<Map.Entry<String, String>> headers) throws IOException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));

        for (java.util.Map.Entry<String, String> entry : headers) {
            builder.header(entry.getKey(), entry.getValue());
        }

        try {
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }

    @Override
    public void close() {
        // HttpClient doesn't need explicit closing
    }
}
