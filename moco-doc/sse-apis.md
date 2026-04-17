# SSE APIs

**@Since 1.6.0**

Server-Sent Events (SSE) streaming support for mocking real-time event streams, including LLM-style token output.

## Basic Usage

```java
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoSse.event;
import static com.github.dreamhead.moco.MocoSse.sse;

HttpServer server = httpServer(12306);
server.request(by(uri("/events")))
      .response(sse(event("message", "Hello")));
```

## API Reference

### MocoSse Factory Class

All SSE APIs are in `MocoSse`:

```java
import static com.github.dreamhead.moco.MocoSse.event;
import static com.github.dreamhead.moco.MocoSse.data;
import static com.github.dreamhead.moco.MocoSse.sse;
```

### sse(SseEvent first, SseEvent... rest)

Creates an SSE streaming response handler with one or more events.

* Java API

```java
server.request(by(uri("/events")))
      .response(sse(
          event("message", "Hello"),
          event("message", " World")
      ));
```

* JSON

```json
{
  "request" : {
    "uri" : "/events"
  },
  "response" : {
    "sse" : [
      { "event": "message", "data": "Hello" },
      { "event": "message", "data": " World" }
    ]
  }
}
```

### sse(Resource resource)

Creates an SSE streaming response from a resource file.

* Java API

```java
server.request(by(uri("/events")))
      .response(sse(file("events.txt")));
```

* JSON

```json
{
  "request" : {
    "uri" : "/events"
  },
  "response" : {
    "sse" : {
      "file" : "events.txt"
    }
  }
}
```

### sse(...).delay(int)

Sets a default delay (ms) for all events without their own delay. Event-level `delay` takes precedence.

* Java API

```java
sse(event("message", "Hello"), event("message", " World")).delay(50)
```

* JSON

```json
{
  "response" : {
    "sse" : {
      "delay" : 50,
      "events" : [
        { "event": "message", "data": "Hello" },
        { "event": "message", "data": " World" }
      ]
    }
  }
}
```

### event(String name, String data, String... rest)

Creates a named SSE event.

* Java API

```java
event("message", "Hello World")
```

### data(String data, String... rest)

Creates an anonymous SSE event (no event name).

* Java API

```java
data("Hello World")
```

### Event Modifiers

Chain modifiers on events:

* Java API

```java
event("message", "Hello").id("1")           // Set event ID
event("message", "Hello").retry(3000)        // Set reconnection time (ms)
event("message", "Hello").delay(50)          // Set send delay (ms), must be > 0
event("message", "Hello").id("1").retry(3000).delay(50)  // Combine modifiers
```

* JSON

```json
{ "event": "message", "data": "Hello", "id": "1", "retry": 3000, "delay": 50 }
```

## Streaming Behavior

SSE responses are **streamed** over a persistent HTTP connection:

- Each event is sent as a separate HTTP chunk
- Events with `delay()` are sent with the specified interval between them
- Use `sse(...).delay(ms)` to set a default delay for all events at once
- Connection is closed after all events are sent
- `delay` is a Moco mock feature, not part of the SSE wire format

### Response Headers

SSE responses automatically include:

- `Content-Type: text/event-stream`
- `Cache-Control: no-cache`
- `Connection: keep-alive`
- `X-Accel-Buffering: no`

## Examples

### LLM Token Streaming

* Java API

```java
server.request(by(uri("/chat")))
      .response(sse(
          event("message", "Hello").delay(50),
          event("message", " World").delay(50),
          event("message", "!").delay(50)
      ));
```

* JSON

```json
{
  "request" : {
    "uri" : "/chat"
  },
  "response" : {
    "sse" : [
      { "event": "message", "data": "Hello", "delay": 50 },
      { "event": "message", "data": " World", "delay": 50 },
      { "event": "message", "data": "!", "delay": 50 }
    ]
  }
}
```

### Multiple Events with IDs

* Java API

```java
server.request(by(uri("/events")))
      .response(sse(
          event("update", "First").id("001"),
          event("update", "Second").id("002")
      ));
```

* JSON

```json
{
  "request" : {
    "uri" : "/events"
  },
  "response" : {
    "sse" : [
      { "event": "update", "data": "First", "id": "001" },
      { "event": "update", "data": "Second", "id": "002" }
    ]
  }
}
```

### Anonymous Data Events

* Java API

```java
server.request(by(uri("/events")))
      .response(sse(
          data("first"),
          data("second")
      ));
```

* JSON

```json
{
  "request" : {
    "uri" : "/events"
  },
  "response" : {
    "sse" : [
      { "data": "first" },
      { "data": "second" }
    ]
  }
}
```

### File-Based Events

* Java API

Create `events.txt`:

```
event: message
data: Hello

event: message
data: World
delay: 50
```

Use it:

```java
server.request(by(uri("/events")))
      .response(sse(file("events.txt")));
```

* JSON

```json
{
  "request" : {
    "uri" : "/events"
  },
  "response" : {
    "sse" : {
      "file" : "events.txt"
    }
  }
}
```

### SSE Event File Format

```
event: message
data: first event

event: message
data: second event
delay: 50

id: 001
event: update
data: third event
```

Fields:
- `id` - Event identifier
- `event` - Event type/name
- `data` - Event data (can appear multiple times for multi-line data)
- `retry` - Reconnection time in milliseconds

Events are separated by blank lines.
