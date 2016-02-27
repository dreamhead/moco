# REST APIs

Restful service is a defacto service design style. Moco provides REST APIs which facilitate restful service stub implementation.

## Basic Usage

You will create a rest server with `restServer` as follows:

```java
RestServer server = restServer(port, log());

ResourceObject resource = new ResourceObject();
resource.code = 1;
resource.message = "hello";

server.resource("targets",
	get("1").response(toJson(resource))
);
```

In this case, you create a resource named `targets`. If you access this resource with `/targets/1` (1 is the ID for this resource) in **GET** method, a resource object will be returned.

**Note:** Restful service is also an HTTP service by default, so HTTP(s) APIs also available to REST server. As you have seen here, `toJson` is a response handler from Moco.

REST APIs is also available in JSON APIs.  The same resource could be created in JSON APIs as follows:

```json
[
"resource": {
  "name": "targets",
  "get": [
    {
      "id": "1",
      "response": {
        "json": {
          "code": 1,
          "message": "foo"
        }
      }
    }
  ]
}
]
```

## Composite REST Settings

Many REST settings could be added with `resource` API as follows:

```java
server.resource("targets",
	get("1").response(toJson(resource1)),
	get("2").response(toJson(resource2)),
    post().response(status(201), header("Location", "/targets/123"))
);
```

## Request and Response

You can also setup request and response for every single REST setting.

```java
server.resource("targets",
	get("1").request(eq(header("Content-Type"), "application/json")).response(toJson(resource))
);
```

Visit [HTTP(s) APIs](apis.md) for more request and response settings.

## Methods

### GET

#### Get with ID

As demonstrated above, it is easy to get a resource with a specified ID. The following setting could be visited with `targets/1` in GET method.

* Java

```java
server.resource("targets",
	get("1").response(toJson(resource))
);
```

* JSON

```
"resource": {
  "name": "targets",
  "get": [
    {
      "id": "1",
      "response": {
        "json": {
          "code": 1,
          "message": "foo"
        }
      }
    }
  ]
}
]
```

#### Get All

If no id specified, get all resource could be created. The following setting could be visited with `targets` in GET method.

* Java

```java
server.resource("targets",
	get().response(toJson(Arrays.asList(resource)))
);
```

* JSON

```JSON
"resource": {
  "name": "targets",
  "get": [
    {
      "response": {
        "json": {
          "code": 1,
          "message": "foo"
        }
      }
    }
  ]
}
]
```