package com.github.dreamhead.moco.helper;

import com.github.dreamhead.moco.sse.SseEvent;
import com.github.dreamhead.moco.sse.SseEventParser;
import com.github.dreamhead.moco.util.ReaderLineIterator;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class SseTestHelper implements Closeable {
    private final CloseableHttpClient client;
    private final String url;
    private ClassicHttpResponse response;
    private Iterator<SseEvent> eventIterator;

    public SseTestHelper(final CloseableHttpClient client, final String url) {
        this.client = client;
        this.url = url;
    }

    @SuppressWarnings("deprecation")
    private void connect() throws IOException {
        HttpGet httpGet = new HttpGet(url);
        response = client.execute(httpGet);
        InputStreamReader reader =
                new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8);
        SseEventParser parser = new SseEventParser();
        eventIterator = parser.parse(() -> new ReaderLineIterator(reader));
    }

    /**
     * Reads a single SSE event frame from the stream.
     * Blocks until a complete event (terminated by blank line) arrives.
     * Returns null when the stream ends.
     */
    public SseEvent readNextEvent() {
        ensureConnected();
        if (eventIterator.hasNext()) {
            return eventIterator.next();
        }
        return null;
    }

    public String getHeader(final String name) {
        ensureConnected();
        return response.getFirstHeader(name).getValue();
    }

    public boolean hasHeader(final String name) {
        ensureConnected();
        return response.getFirstHeader(name) != null;
    }

    private void ensureConnected() {
        if (eventIterator == null) {
            try {
                connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (response != null) {
            response.close();
        }
    }
}
