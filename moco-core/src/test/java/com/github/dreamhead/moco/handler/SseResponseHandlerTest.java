package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.MocoSse;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.resource.ResourceFactory;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SseResponseHandlerTest {

    private HttpRequest mockRequest() {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getVersion()).thenReturn(HttpProtocolVersion.VERSION_1_1);
        return request;
    }

    @Test
    public void should_set_sse_headers() {
        List<SseEvent> events = ImmutableList.of(MocoSse.data("Hello"));
        SseResponseHandler handler = new SseResponseHandler(events);
        DefaultMutableHttpResponse response = DefaultMutableHttpResponse.newResponse(mockRequest(), 200);

        handler.doWriteToResponse(mockRequest(), response);

        assertThat(response.getHeader("Content-Type"), is("text/event-stream"));
        assertThat(response.getHeader("Cache-Control"), is("no-cache"));
        assertThat(response.getHeader("Connection"), is("keep-alive"));
        assertThat(response.getHeader("X-Accel-Buffering"), is("no"));
    }

    @Test
    public void should_mark_response_as_sse() {
        List<SseEvent> events = ImmutableList.of(MocoSse.data("Hello"));
        SseResponseHandler handler = new SseResponseHandler(events);
        DefaultMutableHttpResponse response = DefaultMutableHttpResponse.newResponse(mockRequest(), 200);

        handler.doWriteToResponse(mockRequest(), response);

        assertThat(response.isSse(), is(true));
    }

    @Test
    public void should_store_events_in_response() {
        SseEvent event1 = MocoSse.data("Hello");
        SseEvent event2 = MocoSse.event("message", "World");
        List<SseEvent> events = ImmutableList.of(event1, event2);
        SseResponseHandler handler = new SseResponseHandler(events);
        DefaultMutableHttpResponse response = DefaultMutableHttpResponse.newResponse(mockRequest(), 200);

        handler.doWriteToResponse(mockRequest(), response);

        assertThat(response.getSseEvents().size(), is(2));
        assertThat(response.getSseEvents().get(0), is(event1));
        assertThat(response.getSseEvents().get(1), is(event2));
    }

    @Test
    public void should_not_set_content() {
        List<SseEvent> events = ImmutableList.of(MocoSse.data("Hello"));
        SseResponseHandler handler = new SseResponseHandler(events);
        DefaultMutableHttpResponse response = DefaultMutableHttpResponse.newResponse(mockRequest(), 200);

        handler.doWriteToResponse(mockRequest(), response);

        assertThat(response.getContent(), is(nullValue()));
    }

    @Test
    public void should_set_sse_headers_from_resource() {
        SseResponseHandler handler = (SseResponseHandler) MocoSse.sse(
                ResourceFactory.textResource(request -> "data: Hello\n"));
        DefaultMutableHttpResponse response = DefaultMutableHttpResponse.newResponse(mockRequest(), 200);
        handler.doWriteToResponse(mockRequest(), response);

        assertThat(response.getHeader("Content-Type"), is("text/event-stream"));
        assertThat(response.getHeader("Cache-Control"), is("no-cache"));
    }

    @Test
    public void should_parse_and_store_events_from_resource() {
        SseResponseHandler handler = (SseResponseHandler) MocoSse.sse(
                ResourceFactory.textResource(request -> "data: Hello\n\ndata: World\n"));
        DefaultMutableHttpResponse response = DefaultMutableHttpResponse.newResponse(mockRequest(), 200);
        handler.doWriteToResponse(mockRequest(), response);

        assertThat(response.isSse(), is(true));
        assertThat(response.getSseEvents().size(), is(2));
    }
}
