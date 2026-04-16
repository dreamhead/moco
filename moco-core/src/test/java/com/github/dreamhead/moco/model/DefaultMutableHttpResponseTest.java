package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.MocoSse;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultMutableHttpResponseTest {

    private DefaultMutableHttpResponse newResponse() {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getVersion()).thenReturn(HttpProtocolVersion.VERSION_1_1);
        return DefaultMutableHttpResponse.newResponse(request, 200);
    }

    @Test
    public void should_not_be_sse_by_default() {
        DefaultMutableHttpResponse response = newResponse();
        assertThat(response.isSse(), is(false));
    }

    @Test
    public void should_be_sse_after_setting_events() {
        DefaultMutableHttpResponse response = newResponse();
        response.setSseEvents(ImmutableList.of(MocoSse.data("Hello")));
        assertThat(response.isSse(), is(true));
    }

    @Test
    public void should_store_sse_events() {
        SseEvent event1 = MocoSse.data("Hello");
        SseEvent event2 = MocoSse.event("message", "World");
        DefaultMutableHttpResponse response = newResponse();
        response.setSseEvents(ImmutableList.of(event1, event2));

        Iterator<SseEvent> events = response.getSseEvents().iterator();
        assertThat(events.next(), is(event1));
        assertThat(events.next(), is(event2));
        assertThat(events.hasNext(), is(false));
    }
}
