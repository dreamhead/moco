# APIs
Moco mainly focuses on server configuration. There are only two kinds of API right now: **Request** and **Response**.

That means if we get the expected request and then return our response. Now, you can see a Moco reference in details.

**WARNING** the json configuration below is just a snippet for one pair of request and response, instead of the whole configuration file.

## Request

### Content
If you want to response according to request content, Moco server can be configured as following:

* Java API

```java
server.request(by("foo")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

If request content is too large, you can put it in a file:

* Java API

```java
server.request(by(file("foo.request"))).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "file" : "foo.request"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

### URI
If request uri is your major focus, Moco server could be like this:

* Java API

```java
server.request(by(uri("/foo"))).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "uri" : "/foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```
### Query parameter
Sometimes, your request has parameters in query:

* Java API

```java
server.request(and(by(uri("/foo")), eq(query("param"), "blah"))).response("bar")
```

* JSON

```json
{
  "request" :
    {
      "uri" : "/foo",
      "queries" : 
        {
          "param" : "blah"
        }
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

### HTTP method
It's easy to response based on specified HTTP method:

* Java API

```java
server.get(by(uri("/foo"))).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "method" : "get",
      "uri" : "/foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

Also for POST method:

* Java API

```java
server.post(by("foo")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "method" : "post",
      "text" : "foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

and PUT method:

* Java API

```java
server.put(by("foo")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "method" : "put",
      "text" : "foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

and DELETE method:

* Java API

```java
server.delete(by(uri("/foo"))).response(status(200));
```

* JSON

```json
{
  "request" :
    {
      "method" : "delete",
      "uri" : "/foo"
    },
  "response" :
    {
      "status" : "200"
    }
}
```

If you do need other method, feel free to specify method directly:
* Java API

```java
server.request(method("HEAD")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "method" : "HEAD"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

### Version
We can return different response for different HTTP version:

* Java API

```java
server.request(by(version("HTTP/1.0"))).response("version");
```

* JSON

```json
{
  "request": 
    {
      "version": "HTTP/1.0"
    },
  "response": 
    {
      "text": "version"
    }
}
```

### Header
We will focus HTTP header at times:

* Java API

```java
server.request(eq(header("foo"), "bar")).response("blah")
```

* JSON

```json
{
  "request" :
    {
      "method" : "post",
      "headers" : 
      {
        "content-type" : "application/json"
      }
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

### Cookie
Cookie is widely used in web development.

* Java API

```java
server.request(eq(cookie("loggedIn"), "true")).response(status(200));
```

* JSON

```json
{
  "request" :
    {
      "uri" : "/cookie",
      "cookies" :
        {
          "login" : "true"
        }
    },
  "response" :
    {
      "text" : "success"
    }
}
```

### Form
In web development, form is often used to submit information to server side.

* Java API

```java
server.post(eq(form("name"), "foo")).response("bar");
```

* JSON


```json
{
  "request" :
    {
      "method" : "post",
      "forms" :
        {
          "name" : "foo"
        }
    },
  "response" : 
    {
      "text" : "bar"
    }
}
```

### XML
XML is a popular format for Web Services. When a request is in XML, only the XML structure is important in most cases and whitespace can be ignored. The `xml` operator can be used for this case.

* Java API

```java
server.request(xml(text("<request><parameters><id>1</id></parameters></request>"))).response("foo");
```

* JSON

```json
{
  "request": 
    {
      "uri": "/xml",
      "text": 
        {
          "xml": "<request><parameters><id>1</id></parameters></request>"
        }
    },
  "response": 
    {
      "text": "foo"
    }
}
```

**NOTE**: Please escape the quote in text.

The large request can be put into a file:

```json
{
   "request": 
     {
        "uri": "/xml",
        "file": 
          {
            "xml": "your_file.xml"
          }
    },
  "response": 
    {
      "text": "foo"
    }
}
```

### XPath

For the XML/HTML request, Moco allows us to match request with XPath.

* Java API

```java
server.request(eq(xpath("/request/parameters/id/text()"), "1")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "method" : "post",
      "xpaths" : 
        {
          "/request/parameters/id/text()" : "1"
        }
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

### JSON Request
Json is rising with RESTful style architecture. Just like XML, in the most case, only JSON structure is important, so `json` operator can be used.

* Java API

```java
server.request(json(text("{\"foo\":\"bar\"}"))).response("foo");
```

* JSON

```json
{
  "request": 
    {
      "uri": "/json",
      "text": 
        {
          "json": "{\"foo\":\"bar\"}"
        }
    },
  "response": 
    {
      "text": "foo"
    }
}
```

**NOTE**: Please escape the quote in text.

The large request can be put into a file:

```json
{
  "request": 
    {
      "uri": "/json",
      "file": 
        {
          "json": "your_file.json"
        }
    },
  "response": 
    {
      "text": "foo"
    }
}
```

### JSONPath

For the JSON/HTML request, Moco allows us to match request with JSONPath.

* Java API

```java
server.request(eq(jsonPath("$.book[*].price"), "1")).response("jsonpath match success");
```

* JSON

```json
{
  "request": 
    {
      "uri": "/jsonpath",
      "json_paths": 
        {
          "$.book[*].price": "1"
	}
    },
  "response": 
    {
      "text": "response_for_json_path_request"
    }
}
```

### Match

match is not a functionality, it is an operator. You match your request with regular expression:

* Java API

```java
server.request(match(uri("/\\w*/foo"))).response(text("bar"));
```

* JSON

```json
{
  "request": 
    {
      "uri": 
        {
          "match": "/\\w*/foo"
        }
    },
  "response": 
    {
      "text": "bar"
    }
}
```

Moco is implemented by Java regular expression, you can refer [here](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) for more details.

## Response

### Content

As you have seen in previous example, response with content is pretty easy.

* Java API

```java
server.request(by("foo")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

The same as request, you can response with a file if content is too large to put in a string.

* Java API

```java
server.request(by("foo")).response(file("bar.response"));
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "file" : "bar.response"
    }
}
```

### Status Code
Moco also supports HTTP status code response.

* Java API

```java
server.request(by("foo")).response(status(200));
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "status" : 200
    }
}
```

### Version

By default, response HTTP version is supposed to request HTTP version, but you can set your own HTTP version:

* Java API

```java
server.response(version("HTTP/1.0"));
```

* JSON


```json
{
  "request": 
    {
      "uri": "/version10"
    },
  "response": 
    {
      "version": "HTTP/1.0"
    }
}
```


### Header
We can also specify HTTP header in response.

* Java API

```java
server.request(by("foo")).response(header("content-type", "application/json"));
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "headers" :
        {
          "content-type" : "application/json"
        }
    }
}
```

### Proxy

We can also response with the specified url, just like a proxy.

* Java API

```java
server.request(by("foo")).response(proxy("http://www.github.com"));
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "proxy" : "http://www.github.com"
    }
}
```

Actually, proxy is more powerful than that. It can forward the whole request to the target url, including HTTP method, version, header, content etc.

Besides the basic functionality, proxy also support failover, for example:

* Java API

```java
server.request(by("foo")).response(proxy("http://www.github.com", failover("failover.json")));
```

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "proxy" :
        {
          "url" : "http://localhost:12306/unknown",
          "failover" : "failover.json"
        }
    }
}
```

Proxy will save request/response pair into your failover file. If the proxy target is not reachable, proxy will failover from the file. This feature is very useful for development environment, especially for the case the integration server is not stable.

As the file suffix suggests, this failover file is actually a JSON file, which means you can read/edit it to return whatever you want.

### Redirect

Redirect is a common case for normal web development. We can simply redirect a request to different url.

* Java API

```java
server.get(by(uri("/redirect"))).redirectTo("http://www.github.com");
```

* JSON

```json
{
  "request" :
    {
      "uri" : "/redirect"
    },
  "redirectTo" : "http://www.github.com"
}
```

### Cookie

Cookie can also be in the response.

* Java API

```java
server.response(cookie("loggedIn", "true"), status(302));
```

* JSON

```json
{
  "request" :
    {
      "uri" : "/cookie"
    },
  "response" :
    {
      "cookies" :
      {
        "login" : "true"
      }
    }
}
```


### Latency

Sometimes, we need a latency to simulate slow server side operation.

* Java API

```java
server.request(by("foo")).response(latency(5000));
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "latency" : 5000
    }
}
```

### Sequence

Sometimes, we want to simulate a real-world operation which change server side resource. For example:
* First time you request a resource and "foo" is returned
* We update this resource
* Again request the same URL, updated content, e.g. "bar" is expected.

We can do that by
```java
server.request(by(uri("/foo"))).response(seq("foo", "bar", "blah"));
```

The other response settings are able to be set as well.
```java
server.request(by(uri("/foo"))).response(seq(status(302), status(302), status(200)));
```

## Mount

Moco allows us to mount a directory to uri.

* Java API

```java
server.mount(dir, to("/uri"));
```

* JSON

```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri"
    }
}
```

Wildcard is acceptable to filter specified files, e.g we can include by

* Java API

```java
server.mount(dir, to("/uri"), include("*.txt"));
```

* JSON

```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri",
      "includes" :
        [
          "*.txt"
        ]
    }
}
```

or exclude by

* Java API

```java
server.mount(dir, to("/uri"), exclude("*.txt"));
```

* JSON

```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri",
      "excludes" :
        [
          "*.txt"
        ]
    }
}
```

even compose them by

* Java API

```java
server.mount(dir, to("/uri"), include("a.txt"), exclude("b.txt"), include("c.txt"));
```

* JSON

```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri",
      "includes" :
        [
          "a.txt",
          "c.txt"
        ],
      "excludes" :
        [
          "b.txt"
        ]
    }
}
```

## Template(Beta)
**Note**: Template is an experimental feature which could be changed a lot in the future. Feel free to tell how it helps or you need more feature in template.

Sometimes, we need to customize our response based on something, e.g. response should have same header with request.

The goal can be reached by template:

### Version

With "req.version", request version can be retrieved in template.

The following example will return response version as content.

* Java
```java
server.request(by(uri("/template"))).response(template("${req.version}"));
```

* JSON
```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.version}"
        }
    }
}
```

### Method

Request method is identified by "req.method"

* Java
```java
server.request(by(uri("/template"))).response(template("${req.method}"));
```

* JSON
```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.method}"
        }
    }
}
```

### Content

All request content can be used in template with "req.content"

* Java
```java
server.request(by(uri("/template"))).response(template("${req.content}"));
```

* JSON
```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.content}"
        }
    }
}
```

### Header

Header is another important element in template and we can use "req.headers" for headers.

* Java
```java
server.request(by(uri("/template"))).response(template("${req.headers['foo']"));
```

* JSON
```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.headers['foo']}"
        }
    }
}
```

### Query

"req.queries" helps us to extract request query.

* Java
```java
server.request(by(uri("/template"))).response(template("${req.queries['foo']"));
```

* JSON
```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.queries['foo']}"
        }
    }
}
```

## Event
You may need to request another site when you receive a request, e.g. OAuth. Event could be your helper at that time.

### Complete
Complete event will be fired after your request has been handled completely.

* Java
```java
server.request(by(uri("/event"))).response("event").on(complete(get("http://another_site")));
```

* JSON
```json
{
    "request": {
        "uri" : "/event"
    },
    "response": {
        "text": "event"
    },
    "on": {
        "complete": {
            "get" : {
                "url" : "http://another_site"
            }
        }
    }
}
```

You can post some content as well.

* Java
```java
server.request(by(uri("/event"))).response("event").on(complete(post("http://another_site", "content")));
```

* JSON
```json
{
    "request": {
        "uri" : "/event"
    },
    "response": {
        "text": "event"
    },
    "on": {
        "complete": {
            "post" : {
                "url" : "http://another_site",
                "content": "content"
            }
        }
    }
}
```

Let me know if you need more method.

### Asynchronous
Synchronized request is used by default, which means response won't be returned to client until event handler is finished.

If it is not your expected behavior, you can changed with async API which will fire event asynchronously.

* Java
```java
server.request(by(uri("/event"))).response("event").on(complete(async(post("http://another_site", "content"))));
```

* JSON
```json
{
    "request": {
        "uri" : "/event"
    },
    "response": {
        "text": "event"
    },
    "on": {
        "complete": {
            "async" : "true",
            "post" : {
                "url" : "http://another_site",
                "content": "content"
            }
        }
    }
}
```

You can specify latency for this asynchronous request as well to wait a while.

* Java
```java
server.request(by(uri("/event"))).response("event").on(complete(async(post("http://another_site", "content"), latency(1000))));
```

* JSON
```json
{
    "request": {
        "uri" : "/event"
    },
    "response": {
        "text": "event"
    },
    "on": {
        "complete": {
            "async" : "true",
            "latency" : 1000,
            "post" : {
                "url" : "http://another_site",
                "content": "content"
            }
        }
    }
}
```

# Verify
Someone may want to verify what kind of request has been sent to server in testing framework.

You can verify request like this:

```java
RequestHit hit = requestHit();
final HttpServer server = httpserver(12306, hit);
server.get(by(uri("/foo"))).response("bar");

running(server, new Runnable() {
  @Override
  public void run() throws Exception {
    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
  }
});

hit.verify(by(uri("/foo")), times(1));
```

You can also verify unexpected request like this:
```java
hit.verify(unexpected(), never());
```

Many verification can be used:
* **never**: none of this kind of request has been sent.
* **time**: how many times this kind of request has been sent.
* **atLeast**: at least how many time this kind of request has been sent.
* **atMost**: at least how many time this kind of request has been sent.