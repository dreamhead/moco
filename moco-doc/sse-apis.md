# SSE APIs

Server-Sent Events (SSE) is a server push technology that allows a server to send events to a client over HTTP. Moco provides support for creating SSE mock servers.

## What is SSE?

Server-Sent Events (SSE) is a standard that allows a server to push data to the client whenever new data is available, without the client having to request it. SSE is based on HTTP and uses the `text/event-stream` content type.

### SSE Event Format

```
data: first event

data: second event

event: message
data: Hello World

id: 12345
event: notification
data: New update
retry: 3000

```

Each event consists of:
- `id`: Event identifier
- `event`: Event type/name
- `data`: Event data (can have multiple lines)
- `retry`: Reconnection time in milliseconds

Events are separated by a blank line (`\n\n`).

## Basic Usage

### Simple SSE Response

```java
HttpServer server = httpServer(12306);
server.request(by(uri("/events")))
      .response(sse()
          .data("first event")
          .data("second event")
          .end());
```

### Named Events

```java
server.request(by(uri("/events")))
      .response(sse()
          .event("message", "Hello World")
          .event("notification", "New update")
          .end());
```

### Events with ID

```java
server.request(by(uri("/events")))
      .response(sse()
          .event("message", "Hello World")
          .id("msg-001")
          .end());
```

### Events with Retry

```java
server.request(by(uri("/events")))
      .response(sse()
          .event("message", "Hello World")
          .retry(3000)
          .end());
```

### Events with ID and Retry

```java
server.request(by(uri("/events")))
      .response(sse()
          .event("message", "Hello World")
          .id("msg-001")
          .retry(3000)
          .end());
```

### Multiple Events with Fluent API

```java
server.request(by(uri("/events")))
      .response(sse()
          .event("message", "Hello")
          .id("msg-001")
          .retry(3000)
          .event("notification", "New update")
          .id("notif-002")
          .retry(5000)
          .end());
```

### Empty SSE Response

```java
server.request(by(uri("/events")))
      .response(sse()
          .data("")
          .end());
```

## Response Headers

SSE responses automatically include the following headers:

- `Content-Type: text/event-stream`
- `Cache-Control: no-cache`
- `Connection: keep-alive`
- `X-Accel-Buffering: no` (disables Nginx buffering)

## Testing SSE

### Example Test

```java
@Test
public void should_return_sse_events() throws Exception {
    server.request(by(uri("/sse")))
          .response(sse()
              .data("first event")
              .data("second event")
              .end());

    running(server, () -> {
        String content = helper.executeAsString(
            Request.get("http://localhost:12306/sse")
        );

        assertThat(content, containsString("data: first event"));
        assertThat(content, containsString("data: second event"));
    });
}
```

### Testing Event with ID

```java
@Test
public void should_return_sse_event_with_id() throws Exception {
    server.request(by(uri("/sse")))
          .response(sse()
              .event("message", "Hello World")
              .id("msg-001")
              .end());

    running(server, () -> {
        String content = helper.executeAsString(
            Request.get("http://localhost:12306/sse")
        );

        assertThat(content, containsString("event: message"));
        assertThat(content, containsString("data: Hello World"));
        assertThat(content, containsString("id: msg-001"));
    });
}
```

### Testing Event with Retry

```java
@Test
public void should_return_sse_event_with_retry() throws Exception {
    server.request(by(uri("/sse")))
          .response(sse()
              .event("message", "Hello World")
              .retry(3000)
              .end());

    running(server, () -> {
        String content = helper.executeAsString(
            Request.get("http://localhost:12306/sse")
        );

        assertThat(content, containsString("event: message"));
        assertThat(content, containsString("data: Hello World"));
        assertThat(content, containsString("retry: 3000"));
    });
}
```

### Testing Event with ID and Retry

```java
@Test
public void should_return_sse_event_with_id_and_retry() throws Exception {
    server.request(by(uri("/sse")))
          .response(sse()
              .event("message", "Hello World")
              .id("msg-001")
              .retry(3000)
              .end());

    running(server, () -> {
        String content = helper.executeAsString(
            Request.get("http://localhost:12306/sse")
        );

        assertThat(content, containsString("event: message"));
        assertThat(content, containsString("data: Hello World"));
        assertThat(content, containsString("id: msg-001"));
        assertThat(content, containsString("retry: 3000"));
    });
}
```

## Client-Side Usage

### JavaScript Example

```javascript
const eventSource = new EventSource('http://localhost:12306/events');

eventSource.addEventListener('message', function(e) {
    console.log('Message:', e.data);
});

eventSource.addEventListener('notification', function(e) {
    console.log('Notification:', e.data);
});

eventSource.onerror = function(e) {
    console.error('Error:', e);
};
```

## Use Cases

SSE is particularly useful for:

1. **Real-time Notifications**: Push notifications to clients
2. **Live Updates**: Stock prices, sports scores, news feeds
3. **Progress Updates**: Long-running task progress
4. **Server-Side Events**: Chat messages, activity streams
5. **Monitoring**: Server metrics, log streaming

## SSE vs WebSocket

| Feature | SSE | WebSocket |
|---------|-----|-----------|
| Direction | Server → Client only | Full-duplex |
| Protocol | HTTP | Custom protocol (ws://) |
| Browser Support | Excellent | Excellent |
| Automatic Reconnection | Yes | Manual |
| Binary Data | No (text only) | Yes |

Choose SSE when:
- You only need server-to-client communication
- You want automatic reconnection
- You need to work through proxies
- You prefer simpler implementation

Choose WebSocket when:
- You need bidirectional communication
- You need binary data
- You need low-latency communication

## Complete Example

```java
public class SseServerTest {
    @Test
    public void should_setup_sse_server() throws Exception {
        HttpServer server = httpServer(12306);

        // Setup SSE endpoint with multiple events
        server.request(by(uri("/events")))
              .response(sse()
                  .event("user", "{\"id\":1,\"name\":\"Alice\"}")
                  .id("user-001")
                  .retry(3000)
                  .event("user", "{\"id\":2,\"name\":\"Bob\"}")
                  .id("user-002")
                  .end());

        running(server, () -> {
            String content = helper.executeAsString(
                Request.get("http://localhost:12306/events")
            );

            // Verify first event
            assertThat(content, containsString("event: user"));
            assertThat(content, containsString("data: {\"id\":1,\"name\":\"Alice\"}"));
            assertThat(content, containsString("id: user-001"));
            assertThat(content, containsString("retry: 3000"));

            // Verify second event
            assertThat(content, containsString("data: {\"id\":2,\"name\":\"Bob\"}"));
            assertThat(content, containsString("id: user-002"));
        });
    }
}
```

## API Reference

### Available Methods

The SSE fluent API provides the following methods:

- **`event(eventName, data...)`** - Create a named event
- **`data(data...)`** - Create a data event
- **`id(id)`** - Set event ID for the current event
- **`retry(milliseconds)`** - Set retry time for the current event
- **`end()`** - Complete the SSE response and return handler

### Fluent API Behavior

The API follows Moco's fluent design pattern:

```java
sse()
    .event("message", "Hello")      // Create event
    .id("msg-001")                   // Set ID for current event
    .retry(3000)                      // Set retry for current event
    .event("notification", "Update") // Start new event (auto-saves previous)
    .id("notif-002")                  // Set ID for new event
    .end();                           // Complete and return handler
```

**Key Points:**
- Each call to `event()` or `data()` creates a new event
- The `id()` and `retry()` methods apply to the current event
- When you call `event()` or `data()` again, the previous event is automatically saved
- `end()` saves the last event and returns the response handler
