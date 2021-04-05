# Websocket API

Moco supports websocket api.

**WARNING** the json configuration below is just a snippet for one pair of request and response, instead of the whole configuration file.

## Basis Usage

Websocket server is created from HTTP server.

```java
HttpServer httpServer = httpServer(12306);
WebsocketServer server = httpServer.websocket("/ws");
```

If you want to response according to request content, Moco server can be configured as following:

```java
server.request(by("foo")).response("bar");
```

You can also create websocket server in standalone api.

* JSON

```json
{
  "websocket": {
    "uri": "/ws",
    "sessions": [
      {
        "request": {
          "text": "foo"
        },
        "response": {
          "text": "bar"
        }
      }
    ]
  }
}
```

## Connection Message

Client can receive Websocket server message while connection is established. 

```java
webSocketServer.connected("connected");
```

```json
{
  "websocket": {
    "uri": "/ws",
    "connected": "connected"
  }
}
```

## Ping/Pong

Ping/Pong is usually used as heartbeat in websocket.

```java
websocketServer.ping("ping").pong("pong");
```

```json
{
  "websocket": {
    "uri": "/ws",
    "pingpongs": [
        {
          "ping": "ping",
          "pong": "pong"
        }
    ]
  }
}
```

## Broadcast

You can setup broadcast which means you broadcast your request to all client when you receive request. 

```java
server.request(by("foo")).response(broadcast("bar"));
```

```json
{
  "request": {
    "text": "foo"
  },
  "response": {
    "broadcast": {
      "content": "bar"
    }
  }
}
```

If you just want to broadcast your message to some specific clients, `group` may help you. A client is supposed to join this group before your broadcast as following: 

```java
server.request(by("subscribe")).response(with("subscribed"), join(group("group")));
server.request(by("broadcast")).response(with("broadcasted"), broadcast("content", group("group")));
```

```json
[
  {
    "request": {
      "text": "subscribe"
    },
    "response": {
      "content": "subscribed",
      "group": "group"
    }
  },
  {
    "request": {
      "text": "broadcast"
    },
    "response": {
      "content": "broadcasted",
      "broadcast": {
        "content": "content",
        "group": "group"
      }
    }
  }
]
```
