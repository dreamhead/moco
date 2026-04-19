package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.SseTestHelper;
import com.github.dreamhead.moco.sse.SseEvent;
import org.junit.jupiter.api.Test;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

public class MocoSseStandaloneTest extends AbstractMocoStandaloneTest {

    @Test
    public void should_return_sse_events_from_file() throws Exception {
        runWithConfiguration("sse.json");

        try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
            SseEvent event1 = sse.readNextEvent();
            assertThat(event1.toEventString(), containsString("event: message"));
            assertThat(event1.toEventString(), containsString("data: foo"));

            SseEvent event2 = sse.readNextEvent();
            assertThat(event2.toEventString(), containsString("event: message"));
            assertThat(event2.toEventString(), containsString("data: bar"));
        }
    }

    @Test
    public void should_return_sse_events_from_inline() throws Exception {
        runWithConfiguration("sse.json");

        try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/inline"))) {
            SseEvent event1 = sse.readNextEvent();
            assertThat(event1.toEventString(), containsString("event: message"));
            assertThat(event1.toEventString(), containsString("data: Hello"));

            SseEvent event2 = sse.readNextEvent();
            assertThat(event2.toEventString(), containsString("data: World"));
        }
    }

    @Test
    public void should_stream_events_with_delay() throws Exception {
        int delay = 100;
        int delta = 10;
        runWithConfiguration("sse.json");

        try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/delay"))) {
            sse.readNextEvent();

            long between1and2 = System.currentTimeMillis();
            sse.readNextEvent();
            long elapsed1 = System.currentTimeMillis() - between1and2;

            long between2and3 = System.currentTimeMillis();
            sse.readNextEvent();
            long elapsed2 = System.currentTimeMillis() - between2and3;

            assertThat("Delay between events should be >= 100ms", elapsed1, greaterThanOrEqualTo((long) delay - delta));
            assertThat("Delay between events should be >= 100ms", elapsed2, greaterThanOrEqualTo((long) delay - delta));
        }
    }

    @Test
    public void should_stream_events_with_sse_delay() throws Exception {
        int delay = 100;
        int delta = 10;
        runWithConfiguration("sse.json");

        try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse-delay"))) {
            sse.readNextEvent();

            long between1and2 = System.currentTimeMillis();
            sse.readNextEvent();
            long elapsed1 = System.currentTimeMillis() - between1and2;

            long between2and3 = System.currentTimeMillis();
            sse.readNextEvent();
            long elapsed2 = System.currentTimeMillis() - between2and3;

            assertThat("Delay between events should be >= 100ms", elapsed1, greaterThanOrEqualTo((long) delay - delta));
            assertThat("Delay between events should be >= 100ms", elapsed2, greaterThanOrEqualTo((long) delay - delta));
        }
    }

    @Test
    public void should_stream_events_with_sse_delay_and_time_unit() throws Exception {
        int delay = 100;
        int delta = 10;
        runWithConfiguration("sse.json");

        try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse-delay-unit"))) {
            sse.readNextEvent();

            long between1and2 = System.currentTimeMillis();
            sse.readNextEvent();
            long elapsed1 = System.currentTimeMillis() - between1and2;

            long between2and3 = System.currentTimeMillis();
            sse.readNextEvent();
            long elapsed2 = System.currentTimeMillis() - between2and3;

            assertThat("Delay between events should be >= 100ms", elapsed1, greaterThanOrEqualTo((long) delay - delta));
            assertThat("Delay between events should be >= 100ms", elapsed2, greaterThanOrEqualTo((long) delay - delta));
        }
    }

    @Test
    public void should_stream_events_with_event_delay_and_time_unit() throws Exception {
        int delay = 100;
        int delta = 10;
        runWithConfiguration("sse.json");

        try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/event-delay-unit"))) {
            sse.readNextEvent();

            long between1and2 = System.currentTimeMillis();
            sse.readNextEvent();
            long elapsed1 = System.currentTimeMillis() - between1and2;

            assertThat("Delay between events should be >= 100ms", elapsed1, greaterThanOrEqualTo((long) delay - delta));
        }
    }

    @Test
    public void should_set_sse_headers() throws Exception {
        runWithConfiguration("sse.json");

        try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
            assertThat(sse.getHeader("Content-Type"), is("text/event-stream"));
            assertThat(sse.getHeader("Cache-Control"), is("no-cache"));
        }
    }
}
