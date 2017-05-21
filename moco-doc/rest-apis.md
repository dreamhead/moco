# REST APIs

Restful service is a defacto service design style. Moco provides REST APIs which facilitate restful service stub implementation.

Table of Contents
=================
* [Basic Usage](#basic-usage)
* [Composite REST Settings](#composite-rest-settings)
* [Request and Response](#request-and-response)
* [ID Matcher](#id-matcher)
* [Methods](#methods)
  * [GET](#get)
    * [Get with ID](#get-with-id)
    * [Get All](#get-all)
  * [POST](#post)
  * [PUT](#put)
  * [DELETE](#delete)
  * [HEAD](#head)
    * [HEAD with ID](#head-with-id)
    * [HEAD All](#head-all)
  * [PATCH](#patch)
  * [Sub\-resource](#sub-resource)

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
{
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

You can also setup request and response for every single REST setting as you've done with regular Moco APIs.

```java
server.resource("targets",
	get("1").request(eq(header("Content-Type"), "application/json")).response(toJson(resource))
);
```

Visit [HTTP(s) APIs](apis.md) for more request and response settings.

## ID Matcher

In REST API design, ID is used to identify a specified resource. You can specify a ID with a string.

```java
server.resource("targets",
	get("1").response(toJson(resource))
);
```

Or you can match your single resource with ID matcher. Currently, **anyId** is provided in Moco REST APIs which means the following setting will cover both `/targets/1` and `/targets/2`, even `/targets/anything`. 

```java
server.resource("targets",
	get(anyId()).response(toJson(resource))
);
```

`*` can play as any id in JSON API.

```json
"resource": {
  "name": "targets",
  "get": [
    {
      "id": "*",
      "response": {
        "json": {
          "code": 1,
          "message": "foo"
        }
      }
    }
  ]
}
```

## Methods

### GET

#### Get with ID

**@Since 0.11.0**

In REST API design, GET method is used to query. If an ID is specified, it is used to query a single resource. The following setting could be visited with `/targets/1` in GET method.

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
```

#### Get All

**@Since 0.11.0**

If no id specified, all related resource will be returned. The following setting could be visited with `/targets` in GET method.

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
```

### POST

**@Since 0.11.0**

POST method is used to create new resource. The following setting could be visited with `/targets` in POST method.

* Java

```java
server.resource("targets",
	post().response(status(201), header("Location", "/targets/123"))
);
```

* JSON

```json
"resource": {
  "name": "targets",
  "post": [
    {
      "response": {
        "status": 201,
        "headers" : {
          "Location": "/targets/123"
		}
      }
    }
  ]
}
```

### PUT

**@Since 0.11.0**

PUT method is used to update a specified resource. The following setting could be visited with `/targets/1` in PUT method.

* Java

```java
server.resource("targets",
	put("1").response(status(200))
);
```

* JSON

```json
"resource": {
  "name": "targets",
  "put": [
    {
      "id": 1,
      "response": {
        "status": 200,
      }
    }
  ]
}
```

### DELETE

**@Since 0.11.0**

DELETE method is used to delete a specified resource. The following setting could be visited with `/targets/1` in DELETE method.

* Java

```java
server.resource("targets",
	delete("1").response(status(200))
);
```

* JSON

```json
"resource": {
  "name": "targets",
  "delete": [
    {
      "id": 1,
      "response": {
        "status": 200,
      }
    }
  ]
}
```

### HEAD

#### HEAD with ID

**@Since 0.11.0**

HEAD method is used to query resource metadata. If an ID is specified, it is used to query a single resource. The following setting could be visited with `/targets/1` in HEAD method.

* Java

```java
server.resource("targets",
	head("1").response(header("ETag", "Moco"))
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
        "headers": {
          "ETag": "Moco"
        }
      }
    }
  ]
}
```

#### HEAD All

**@Since 0.11.0**

If no id specified, all related resource metadata will be returned. The following setting could be visited with `/targets` in HEAD method.

* Java

```java
server.resource("targets",
	head().response(header("ETag", "Moco"))
);
```

* JSON

```
"resource": {
  "name": "targets",
  "get": [
    {
      "response": {
        "headers": {
          "ETag": "Moco"
        }
      }
    }
  ]
}
```

### PATCH

**@Since 0.11.0**

PATCH method is used to update a specified resource. The following setting could be visited with `/targets/1` in PATCH method.

- Java

```java
server.resource("targets",
	patch("1").response(status(200))
);
```

- JSON

```json
"resource": {
  "name": "targets",
  "patch": [
    {
      "id": 1,
      "response": {
        "status": 200,
      }
    }
  ]
}
```

### Sub-resource

**@Since 0.11.0**

Sub-resource is used to build relationship for resource. The following setting could be visited with `/targets/1/subs/1` in GET method.

* Java

```java
server.resource("targets",
	id("1").name("subs").settings(
      get("1").response(toJson(resource)),
    )
);
```

* JSON

```json
"resource": {
  "name": "targets",
  "resource": [
  	{
      "id": "1",
      "name": "subs",
      "get": [
      	{
          "id": "1",
          "response": {
            "json": {
              "code": 3,
              "message": "sub"
            }
          }
        }
      ]
	}
  ]
}
```

The REST settings in sub-resource are the same as regular REST API.