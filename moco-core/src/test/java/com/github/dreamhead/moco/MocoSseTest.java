package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.SseTestHelper;
import com.github.dreamhead.moco.sse.SseEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.proxy;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoSse.data;
import static com.github.dreamhead.moco.MocoSse.event;
import static com.github.dreamhead.moco.MocoSse.sse;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MocoSseTest extends AbstractMocoHttpTest {

    @Test
    public void should_reject_null_first_event() {
        assertThrows(NullPointerException.class, () -> MocoSse.sse(null));
    }

    @Test
    public void should_reject_null_event_name() {
        assertThrows(NullPointerException.class, () -> MocoSse.event(null, "data"));
    }

    @Test
    public void should_reject_null_data() {
        assertThrows(NullPointerException.class, () -> MocoSse.data(null));
    }

    @Test
    public void should_return_sse_events_frame_by_frame() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(
                  event("message", "Hello"),
                  event("message", " World")
              ));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                SseEvent event1 = sse.readNextEvent();
                assertThat(event1.toEventString(), containsString("event: message"));
                assertThat(event1.toEventString(), containsString("data: Hello"));

                SseEvent event2 = sse.readNextEvent();
                assertThat(event2.toEventString(), containsString("event: message"));
                assertThat(event2.toEventString(), containsString("data: World"));
            }
        });
    }

    @Test
    public void should_return_data_events_frame_by_frame() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(
                  data("first"),
                  data("second")
              ));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                SseEvent event1 = sse.readNextEvent();
                assertThat(event1.toEventString(), containsString("data: first"));

                SseEvent event2 = sse.readNextEvent();
                assertThat(event2.toEventString(), containsString("data: second"));
            }
        });
    }

    @Test
    public void should_not_have_content_length_for_streaming() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(event("message", "Hello")));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                assertThat(sse.getHeader("Content-Type"), is("text/event-stream"));
                assertThat(sse.hasHeader("Content-Length"), is(false));
            }
        });
    }

    @Test
    public void should_stream_events_with_delay() throws Exception {
        int delay = 100;
        int delta = 10;
        server.request(by(uri("/sse")))
              .response(sse(
                  event("message", "token1").delay(delay),
                  event("message", "token2").delay(delay),
                  event("message", "token3").delay(delay)
              ));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
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
        });
    }

    @Test
    public void should_stream_events_with_sse_delay() throws Exception {
        int delay = 100;
        int delta = 10;
        server.request(by(uri("/sse")))
              .response(sse(
                  event("message", "token1"),
                  event("message", "token2"),
                  event("message", "token3")
              ).delay(delay));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
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
        });
    }

    @Test
    public void should_stream_events_with_delay_and_time_unit() throws Exception {
        int delay = 100;
        int delta = 10;
        server.request(by(uri("/sse")))
              .response(sse(
                  event("message", "token1").delay(delay, TimeUnit.MILLISECONDS),
                  event("message", "token2").delay(delay, TimeUnit.MILLISECONDS),
                  event("message", "token3").delay(delay, TimeUnit.MILLISECONDS)
              ));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
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
        });
    }

    @Test
    public void should_stream_events_with_sse_delay_and_time_unit() throws Exception {
        int delay = 100;
        int delta = 10;
        server.request(by(uri("/sse")))
              .response(sse(
                  event("message", "token1"),
                  event("message", "token2"),
                  event("message", "token3")
              ).delay(delay, TimeUnit.MILLISECONDS));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
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
        });
    }

    @Test
    public void should_stream_events_without_delay_quickly() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(
                  event("message", "a"),
                  event("message", "b"),
                  event("message", "c")
              ));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                sse.readNextEvent();

                long start = System.currentTimeMillis();
                sse.readNextEvent();
                sse.readNextEvent();
                long elapsed = System.currentTimeMillis() - start;

                assertThat("Events without delay should arrive quickly", elapsed, lessThan(100L));
            }
        });
    }

    @Test
    public void should_set_correct_content_type() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(data("test")));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                assertThat(sse.getHeader("Content-Type"), is("text/event-stream"));
            }
        });
    }

    @Test
    public void should_set_cache_control_header() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(data("test")));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                assertThat(sse.getHeader("Cache-Control"), is("no-cache"));
            }
        });
    }

    @Test
    public void should_return_event_with_id() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(data("Hello").id("msg-001")));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                SseEvent event1 = sse.readNextEvent();
                assertThat(event1.toEventString(), containsString("id: msg-001"));
                assertThat(event1.toEventString(), containsString("data: Hello"));
            }
        });
    }

    @Test
    public void should_return_event_with_retry() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(data("Hello").retry(3000)));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                SseEvent event1 = sse.readNextEvent();
                assertThat(event1.toEventString(), containsString("retry: 3000"));
                assertThat(event1.toEventString(), containsString("data: Hello"));
            }
        });
    }

    @Test
    public void should_return_multiple_events_with_different_ids() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse(
                  event("update", "First").id("001"),
                  event("update", "Second").id("002")
              ));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/sse"))) {
                SseEvent event1 = sse.readNextEvent();
                assertThat(event1.toEventString(), containsString("id: 001"));
                assertThat(event1.toEventString(), containsString("data: First"));

                SseEvent event2 = sse.readNextEvent();
                assertThat(event2.toEventString(), containsString("id: 002"));
                assertThat(event2.toEventString(), containsString("data: Second"));
            }
        });
    }

    @Test
    public void should_proxy_sse_events() throws Exception {
        server.request(by(uri("/target")))
              .response(sse(
                  event("message", "Hello"),
                  event("message", "World")
              ));
        server.request(by(uri("/proxy")))
              .response(proxy(remoteUrl("/target")));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/proxy"))) {
                assertThat(sse.getHeader("Content-Type"), is("text/event-stream"));

                SseEvent event1 = sse.readNextEvent();
                assertThat(event1.toEventString(), containsString("event: message"));
                assertThat(event1.toEventString(), containsString("data: Hello"));

                SseEvent event2 = sse.readNextEvent();
                assertThat(event2.toEventString(), containsString("event: message"));
                assertThat(event2.toEventString(), containsString("data: World"));
            }
        });
    }

    @Test
    public void should_proxy_sse_events_with_delay() throws Exception {
        int delay = 100;
        int delta = 10;
        server.request(by(uri("/target")))
              .response(sse(
                  event("message", "first").delay(delay),
                  event("message", "second").delay(delay)
              ));
        server.request(by(uri("/proxy")))
              .response(proxy(remoteUrl("/target")));

        running(server, () -> {
            try (SseTestHelper sse = new SseTestHelper(helper.getClient(), remoteUrl("/proxy"))) {
                long start = System.currentTimeMillis();
                sse.readNextEvent();
                long firstElapsed = System.currentTimeMillis() - start;
                assertThat("First event should arrive quickly", firstElapsed, lessThan((long) delay));

                long between1and2 = System.currentTimeMillis();
                sse.readNextEvent();
                long elapsed = System.currentTimeMillis() - between1and2;

                assertThat("Delay between proxied events should be preserved",
                        elapsed, greaterThanOrEqualTo((long) delay - delta));
            }
        });
    }
}
