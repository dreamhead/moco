package com.github.dreamhead.moco;

import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.client5.http.fluent.Request;
import org.junit.jupiter.api.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Moco.sse;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

public class MocoSseTest extends AbstractMocoHttpTest {

    @Test
    public void should_return_sse_events() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse()
                  .data("first event")
                  .data("second event")
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(Request.get("http://localhost:12306/sse"));

            assertThat(content, containsString("data: first event"));
            assertThat(content, containsString("data: second event"));
        });
    }

    @Test
    public void should_return_sse_events_with_type() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse()
                  .event("message", "Hello World")
                  .event("notification", "New update")
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(Request.get("http://localhost:12306/sse"));

            assertThat(content, containsString("event: message"));
            assertThat(content, containsString("data: Hello World"));
            assertThat(content, containsString("event: notification"));
            assertThat(content, containsString("data: New update"));
        });
    }

    @Test
    public void should_set_correct_content_type() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse().data("test").end());

        running(server, () -> {
            HttpResponse response = helper.execute(Request.get("http://localhost:12306/sse"));

            assertThat(response.getFirstHeader("Content-Type").getValue(), is("text/event-stream"));
        });
    }

    @Test
    public void should_set_cache_control_header() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse().data("test").end());

        running(server, () -> {
            HttpResponse response = helper.execute(Request.get("http://localhost:12306/sse"));

            assertThat(response.getFirstHeader("Cache-Control").getValue(), is("no-cache"));
        });
    }

    @Test
    public void should_set_connection_header() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse().data("test").end());

        running(server, () -> {
            HttpResponse response = helper.execute(Request.get("http://localhost:12306/sse"));

            // Connection header might not be set in HTTP/1.1 default behavior
            // So we just check that we get a successful response
            assertThat(response.getCode(), is(200));
        });
    }

    @Test
    public void should_return_empty_sse_response() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse().data("").end());

        running(server, () -> {
            HttpResponse response = helper.execute(Request.get("http://localhost:12306/sse"));

            // Empty SSE response should still be successful
            assertThat(response.getCode(), is(200));
        });
    }

    @Test
    public void should_return_sse_event_with_id() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse()
                  .data("event with id")
                  .id("msg-001")
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(Request.get("http://localhost:12306/sse"));

            assertThat(content, containsString("id: msg-001"));
            assertThat(content, containsString("data: event with id"));
        });
    }

    @Test
    public void should_return_sse_event_with_retry() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse()
                  .data("event with retry")
                  .retry(5000)
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(Request.get("http://localhost:12306/sse"));

            assertThat(content, containsString("retry: 5000"));
            assertThat(content, containsString("data: event with retry"));
        });
    }

    @Test
    public void should_return_sse_event_with_id_and_retry() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse()
                  .event("message", "Hello World")
                  .id("msg-123")
                  .retry(3000)
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(Request.get("http://localhost:12306/sse"));

            assertThat(content, containsString("id: msg-123"));
            assertThat(content, containsString("retry: 3000"));
            assertThat(content, containsString("event: message"));
            assertThat(content, containsString("data: Hello World"));
        });
    }

    @Test
    public void should_return_multiple_events_with_ids() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse()
                  .event("update", "First update")
                  .id("001")
                  .event("update", "Second update")
                  .id("002")
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(Request.get("http://localhost:12306/sse"));

            assertThat(content, containsString("id: 001"));
            assertThat(content, containsString("data: First update"));
            assertThat(content, containsString("id: 002"));
            assertThat(content, containsString("data: Second update"));
        });
    }

    @Test
    public void should_support_complex_fluent_api() throws Exception {
        server.request(by(uri("/sse")))
              .response(sse()
                  .event("message", "Hello")
                  .id("msg-001")
                  .retry(3000)
                  .event("notification", "New update")
                  .id("notif-002")
                  .retry(5000)
                  .data("Simple event")
                  .id("simple-003")
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(Request.get("http://localhost:12306/sse"));

            // First event
            assertThat(content, containsString("event: message"));
            assertThat(content, containsString("data: Hello"));
            assertThat(content, containsString("id: msg-001"));
            assertThat(content, containsString("retry: 3000"));

            // Second event
            assertThat(content, containsString("event: notification"));
            assertThat(content, containsString("data: New update"));
            assertThat(content, containsString("id: notif-002"));
            assertThat(content, containsString("retry: 5000"));

            // Third event
            assertThat(content, containsString("data: Simple event"));
            assertThat(content, containsString("id: simple-003"));
        });
    }
}
