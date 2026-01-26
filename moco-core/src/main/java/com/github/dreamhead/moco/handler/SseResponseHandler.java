package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;
import java.util.List;

public class SseResponseHandler extends AbstractHttpResponseHandler {
    private static final MediaType SSE_CONTENT_TYPE = MediaType.create("text", "event-stream");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final List<SseEvent> events;

    public SseResponseHandler(final List<SseEvent> events) {
        this.events = events != null ? ImmutableList.copyOf(events) : ImmutableList.of();
    }

    public final List<SseEvent> getEvents() {
        return events;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        // Set SSE specific headers
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, SSE_CONTENT_TYPE.toString());
        httpResponse.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        httpResponse.addHeader(HttpHeaders.CONNECTION, "keep-alive");
        httpResponse.addHeader("X-Accel-Buffering", "no"); // Disable Nginx buffering

        // Build SSE content
        String sseContent = buildSseContent();
        MessageContent content = MessageContent.content()
                .withContent(sseContent)
                .withCharset(UTF_8)
                .build();

        httpResponse.setContent(content);
    }

    private String buildSseContent() {
        if (events.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (SseEvent event : events) {
            sb.append(event.toEventString());
        }
        return sb.toString();
    }

    @Override
    public ResponseHandler doApply(final MocoConfig config) {
        // SSE events are not configuration-dependent
        return this;
    }
}
