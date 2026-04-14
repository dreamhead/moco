package com.github.dreamhead.moco.sse;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SseEventParserTest {
    private static final Splitter LINE_SPLITTER = Splitter.on('\n');
    private final SseEventParser parser = new SseEventParser();

    private List<SseEvent> parse(final String content) {
        return ImmutableList.copyOf(parser.parse(LINE_SPLITTER.split(content)));
    }

    @Test
    public void should_parse_single_event() {
        List<SseEvent> events = parse("event: message\ndata: Hello\n");
        assertThat(events.size(), is(1));
        assertThat(events.get(0).toEventString(), containsString("event: message"));
        assertThat(events.get(0).toEventString(), containsString("data: Hello"));
    }

    @Test
    public void should_parse_multiple_events() {
        List<SseEvent> events = parse("event: message\ndata: Hello\n\nevent: message\ndata: World\n");
        assertThat(events.size(), is(2));
    }

    @Test
    public void should_parse_event_with_id() {
        List<SseEvent> events = parse("id: 1\ndata: Hello\n");
        assertThat(events.size(), is(1));
        assertThat(events.get(0).toEventString(), containsString("id: 1"));
    }

    @Test
    public void should_parse_event_with_retry() {
        List<SseEvent> events = parse("data: Hello\nretry: 3000\n");
        assertThat(events.size(), is(1));
        assertThat(events.get(0).toEventString(), containsString("retry: 3000"));
    }

    @Test
    public void should_parse_multi_data_event() {
        List<SseEvent> events = parse("data: line1\ndata: line2\n");
        assertThat(events.size(), is(1));
        assertThat(events.get(0).toEventString(), containsString("data: line1"));
        assertThat(events.get(0).toEventString(), containsString("data: line2"));
    }

    @Test
    public void should_throw_on_missing_data() {
        assertThrows(IllegalArgumentException.class, () -> parse("event: message\n"));
    }

    @Test
    public void should_handle_empty_content() {
        List<SseEvent> events = ImmutableList.copyOf(parser.parse(ImmutableList.of()));
        assertThat(events.size(), is(0));
    }

    @Test
    public void should_strip_multiple_spaces_after_colon() {
        List<SseEvent> events = parse("data:   Hello\n");
        assertThat(events.size(), is(1));
        assertThat(events.get(0).toEventString(), containsString("data: Hello"));
    }

    @Test
    public void should_handle_no_space_after_colon() {
        List<SseEvent> events = parse("data:Hello\n");
        assertThat(events.size(), is(1));
        assertThat(events.get(0).toEventString(), containsString("data: Hello"));
    }

    @Test
    public void should_handle_whitespace_only_lines() {
        List<SseEvent> events = parse("   ");
        assertThat(events.size(), is(0));
    }
}
