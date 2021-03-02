# Websocket API

Moco supports websocket api.

**WARNING** the json configuration below is just a snippet for one pair of request and response, instead of the whole configuration file.

# Basis Usage

Websocket server is created from HTTP server.

```java
HttpServer server = httpServer(12306);
WebsocketServer webSocketServer = server.websocket("/ws");
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
    "pingpongs": [
      {
        "ping": "ping",
        "pong": "pong"
      }
    ],
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