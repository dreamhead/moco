package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.sse.SseEvent;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class DefaultHttpResponseTest {

    @Test
    public void should_serialize_response_with_sse_events() {
        SseEvent event1 = SseEvent.event("message", ImmutableList.of("Hello"));
        SseEvent event2 = SseEvent.event("message", ImmutableList.of("World"));
        HttpResponse response = DefaultHttpResponse.builder()
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withStatus(200)
                .withSseEvents(ImmutableList.of(event1, event2))
                .build();

        String json = Jsons.toJson(response);
        assertThat(json, containsString("sseEvents"));
        assertThat(json, containsString("Hello"));
        assertThat(json, containsString("World"));
    }

    @Test
    public void should_deserialize_response_with_sse_events() {
        SseEvent event1 = SseEvent.event("message", ImmutableList.of("Hello"));
        SseEvent event2 = SseEvent.event("message", ImmutableList.of("World"));
        HttpResponse response = DefaultHttpResponse.builder()
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withStatus(200)
                .withSseEvents(ImmutableList.of(event1, event2))
                .build();

        String json = Jsons.toJson(response);
        HttpResponse deserialized = Jsons.toObject(json, DefaultHttpResponse.class);
        List<SseEvent> events = StreamSupport.stream(deserialized.getSseEvents().spliterator(), false).toList();
        assertThat(events.size(), is(2));
        assertThat(events.get(0), is(event1));
        assertThat(events.get(1), is(event2));
    }

    @Test
    public void should_deserialize_response_without_sse_events() {
        HttpResponse response = DefaultHttpResponse.builder()
                .withVersion(HttpProtocolVersion.VERSION_1_1)
                .withStatus(200)
                .withStringContent("hello")
                .build();

        String json = Jsons.toJson(response);
        HttpResponse deserialized = Jsons.toObject(json, DefaultHttpResponse.class);
        assertThat(deserialized.getSseEvents(), is(nullValue()));
        assertThat(deserialized.getContent().toString(), is("hello"));
    }

    @Test
    public void should_be_backward_compatible_with_old_failover_format() {
        String json = "{\n"
                + "  \"version\" : \"HTTP/1.1\",\n"
                + "  \"content\" : \"proxy\",\n"
                + "  \"headers\" : {\n"
                + "    \"Content-Type\" : \"text/plain; charset=UTF-8\"\n"
                + "  },\n"
                + "  \"status\" : 200\n"
                + "}";

        HttpResponse deserialized = Jsons.toObject(json, DefaultHttpResponse.class);
        assertThat(deserialized.getStatus(), is(200));
        assertThat(deserialized.getSseEvents(), is(nullValue()));
    }
}
