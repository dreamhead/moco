package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.dreamhead.moco.sse.SseEvent.data;
import static com.github.dreamhead.moco.sse.SseEvent.event;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SseResponseHandlerTest {

    private SseResponseHandler handler;
    private HttpRequest request;
    private MutableHttpResponse response;

    @BeforeEach
    public void setUp() {
        List<SseEvent> events = ImmutableList.of(
                data("First event").build(),
                event("message", "Hello World").build()
        );
        handler = new SseResponseHandler(events);
        request = mock(HttpRequest.class);
        response = mock(MutableHttpResponse.class);
    }

    @Test
    public void should_set_sse_content_type() {
        handler.doWriteToResponse(request, response);

        verify(response).addHeader("Content-Type", "text/event-stream");
    }

    @Test
    public void should_set_cache_control_header() {
        handler.doWriteToResponse(request, response);

        verify(response).addHeader("Cache-Control", "no-cache");
    }

    @Test
    public void should_set_connection_header() {
        handler.doWriteToResponse(request, response);

        verify(response).addHeader("Connection", "keep-alive");
    }

    @Test
    public void should_set_accel_buffering_header() {
        handler.doWriteToResponse(request, response);

        verify(response).addHeader("X-Accel-Buffering", "no");
    }

    @Test
    public void should_get_events() {
        List<SseEvent> events = handler.getEvents();

        assertThat(events.size(), is(2));
    }
}
