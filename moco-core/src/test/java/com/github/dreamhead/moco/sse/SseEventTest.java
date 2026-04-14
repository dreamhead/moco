package com.github.dreamhead.moco.sse;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SseEventTest {

    @Test
    public void should_create_named_event() {
        SseEvent event = SseEvent.event("message", ImmutableList.of("Hello"));
        assertThat(event.toEventString(), containsString("event: message\n"));
        assertThat(event.toEventString(), containsString("data: Hello\n"));
    }

    @Test
    public void should_create_data_event() {
        SseEvent event = SseEvent.data(ImmutableList.of("Hello"));
        assertThat(event.toEventString(), is("data: Hello\n\n"));
    }

    @Test
    public void should_create_event_with_id() {
        SseEvent event = SseEvent.data(ImmutableList.of("Hello")).id("1");
        assertThat(event.toEventString(), containsString("id: 1\n"));
        assertThat(event.toEventString(), containsString("data: Hello\n"));
    }

    @Test
    public void should_create_event_with_retry() {
        SseEvent event = SseEvent.data(ImmutableList.of("Hello")).retry(3000);
        assertThat(event.toEventString(), containsString("retry: 3000\n"));
    }

    @Test
    public void should_create_event_with_delay() {
        SseEvent event = SseEvent.data(ImmutableList.of("Hello")).delay(50);
        assertThat(event.getDelay(), is(50));
        assertThat(event.toEventString(), is("data: Hello\n\n"));
    }

    @Test
    public void should_reject_zero_delay() {
        assertThrows(IllegalArgumentException.class, () -> SseEvent.data(ImmutableList.of("Hello")).delay(0));
    }

    @Test
    public void should_reject_negative_delay() {
        assertThrows(IllegalArgumentException.class, () -> SseEvent.data(ImmutableList.of("Hello")).delay(-1));
    }

    @Test
    public void should_support_multi_data() {
        SseEvent event = SseEvent.data(ImmutableList.of("line1", "line2"));
        assertThat(event.toEventString(), containsString("data: line1\n"));
        assertThat(event.toEventString(), containsString("data: line2\n"));
    }

    @Test
    public void should_support_full_event() {
        SseEvent event = SseEvent.event("message", ImmutableList.of("Hello")).id("1").retry(3000).delay(50);
        String result = event.toEventString();
        assertThat(result, containsString("id: 1\n"));
        assertThat(result, containsString("event: message\n"));
        assertThat(result, containsString("retry: 3000\n"));
        assertThat(result, containsString("data: Hello\n"));
        assertThat(event.getDelay(), is(50));
    }

    @Test
    public void should_be_immutable() {
        SseEvent original = SseEvent.data(ImmutableList.of("Hello"));
        SseEvent withId = original.id("1");
        SseEvent withRetry = withId.retry(3000);

        assertThat(original.toEventString(), is("data: Hello\n\n"));
        assertThat(withId.toEventString(), containsString("id: 1\n"));
        assertThat(withId.toEventString(), not(containsString("retry:")));
        assertThat(withRetry.toEventString(), containsString("id: 1\n"));
        assertThat(withRetry.toEventString(), containsString("retry: 3000\n"));
    }
}
