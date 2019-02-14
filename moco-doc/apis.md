# HTTP(s) APIs
Moco mainly focuses on server configuration. There are only two kinds of API right now: **Request** and **Response**.

That means if we get the expected request and then return our response. Now, you can see a Moco reference in details.

**WARNING** the json configuration below is just a snippet for one pair of request and response, instead of the whole configuration file.


Table of Contents
=================

* [Composite Java API Design](#composite-java-api-design)
* [Description as comment](#description-as-comment)
* [Request](#request)
  * [Content](#content)
  * [URI](#uri)
  * [Query Parameter](#query-parameter)
  * [HTTP Method](#http-method)
  * [Version](#version)
  * [Header](#header)
  * [Cookie](#cookie)
  * [Form](#form)
  * [XML](#xml)
  * [XPath](#xpath)
  * [JSON Request](#json-request)
    * [JSON Text](#json-text)
    * [JSON Shortcut](#json-shortcut)
    * [JSON File](#json-file)
  * [JSONPath](#jsonpath)
  * [Operator](#operator)
    * [Match](#match)
    * [Starts With](#starts-with)
    * [Ends With](#ends-with)
    * [Contain](#contain)
    * [Exist](#exist)
* [Response](#response)
  * [Content](#content-1)
  * [Status Code](#status-code)
  * [Version](#version-1)
  * [Header](#header-1)
  * [Proxy](#proxy)
    * [Single URL](#single-url)
    * [Failover](#failover)
    * [Playback](#playback)
    * [Customize Failover/Playback Status](#customize-failoverplayback-status)
    * [Batch URLs](#batch-urls)
  * [Redirect](#redirect)
  * [Cookie](#cookie-1)
    * [Cookie Attributes](#cookie-attributes)
      * [Path](#path)
      * [Domain](#domain)
      * [Secure](#secure)
      * [HTTP Only](#http-only)
      * [Max Age](#max-age)
  * [Attachment](#attachment)
  * [Latency](#latency)
  * [Sequence](#sequence)
  * [JSON Response](#json-response)
* [Mount](#mount)
* [Template(Beta)](#templatebeta)
  * [Request](#request-1)
    * [Version](#version-2)
    * [Method](#method)
    * [Content](#content-2)
    * [Header](#header-2)
    * [Query](#query)
    * [Form](#form-1)
    * [Cookie](#cookie-2)
    * [JSON](#json)
  * [Custom Variable](#custom-variable)
  * [Template Function](#template-function)
    * [now](#now)
    * [random](#random)
  * [Redirect](#redirect-1)
  * [File Name Template](#file-name-template)
  * [Proxy](#proxy-1)
  * [Template for Event Action](#template-for-event-action)
* [Event](#event)
  * [Complete](#complete)
    * [Get Request](#get-request)
    * [Post Request](#post-request)
  * [Asynchronous](#asynchronous)
* [Verify](#verify)
* [Miscellaneous](#miscellaneous)
  * [Port](#port)
  * [Log](#log)
    * [Log with verifier](#log-with-verifier)

## Composite Java API Design
Moco Java API is designed in functional fashion which means you can composite any request or response easily.

```java
server.request(and(by(uri("/target")), by(version(VERSION_1_0)))).response(with(text("foo")), header("Content-Type", "text/html"));
```

## Description as comment
**@Since 0.7**

In all JSON APIs, you can use **description** to describe what is this session for. It's just used as comment, which will be ignored in runtime.

```json
[
    {
        "description": "any response",
        "response": {
            "text": "foo"
        }
    }
]
```

## Request

### Content
**@Since 0.7**

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
**@Since 0.7**

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
### Query Parameter
**@Since 0.7**

Sometimes, your request has parameters in query:

* Java API

```java
server.request(and(by(uri("/foo")), eq(query("param"), "blah"))).response("bar");
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

### HTTP Method
**@Since 0.7**

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
server.request(by(method("HEAD"))).response("bar");
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
**@Since 0.7**

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
**@Since 0.7**

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
**@Since 0.7**

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
**@Since 0.7**

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
**@Since 0.7**

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
**@Since 0.7**

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

#### JSON Text
**@Since 0.7**

Json is rising with RESTful style architecture. Just like XML, in the most case, only JSON structure is important, so `json` operator can be used.

* Java API

```java
server.request(json(text("{\"foo\":\"bar\"}"))).response("foo");
```


**@Since 0.12.0**
`json` will return a resource from next release

```java
server.request(by(json(text("{\"foo\":\"bar\"}")))).response("foo");
```

Note that this functionality is implemented in Jackson, please make sure your POJO is written in Jackson acceptable format.

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

#### JSON Shortcut

If the response is JSON, we don't need to write JSON text with escape character in code.

**@Since 0.10.2**

You can give a POJO to Java API, it will be converted JSON text.

```java
server.request(json(pojo)).response("foo");
```

**@Since 0.12.0**
`json` will return a resource from next release

```java
server.request(by(json(pojo))).response("foo");
```

**@Since 0.9.2**

As you have seen, it is so boring to write json with escape character, especially in json configuration. So you can try the json shortcut. The upper case could be rewritten as following:

* JSON

```json
{
    "request": {
        "uri": "/json",
        "json": {
            "foo": "bar"
        }
    },
    "response": {
        "text": "foo"
    }
}
```

#### JSON File
**@Since 0.7**

The large request can be put into a file:

* Java API

```java
server.request(json(file("your_file.json"))).response("foo");
```

* JSON

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
**@Since 0.7**

For the JSON/HTML request, Moco allows us to match request with JSONPath.

* Java API

```java
server.request(eq(jsonPath("$.book[*].price"), "1")).response("response_for_json_path_request");
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

### Operator

Moco also supports some operators which helps you write your expectation easily.

#### Match
**@Since 0.7**

You may want to match your request with regular expression, **match** could be your helper:

* Java API

```java
server.request(match(uri("/\\w*/foo"))).response("bar");
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

#### Starts With
**@Since 0.9.2**

**starsWith** operator can help you decide if the request information starts with a piece of text.

* Java API

```java
server.request(startsWith(uri("/foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "uri":
        {
          "startsWith": "/foo"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

#### Ends With
**@Since 0.9.2**

**endsWith** operator can help you decide if the request information ends with a piece of text.

* Java API

```java
server.request(endsWith(uri("foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "uri":
        {
          "endsWith": "foo"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

#### Contain
**@Since 0.9.2**

**contain** operator helps you know whether the request information contains a piece of text.

* Java API

```java
server.request(contain(uri("foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "uri":
        {
          "contain": "foo"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

#### Exist
**@Since 0.9.2**

**exist** operator is used to decide whether the request information exists.

* Java API

```java
server.request(exist(header("foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "headers": {
        "foo": {
          "exist" : "true"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

For JSON API, you can decide whether the information does not exist.

* JSON

```json
{
  "request":
    {
      "headers": {
        "foo": {
          "exist" : "not"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

## Response

### Content
**@Since 0.7**

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

**@Since 0.10.1**

You can specify file charset if you want to see it in correct encoding in console.

* Java API

```java
server.response(file("src/test/resources/gbk.response", Charset.forName("GBK")));
```

* JSON

```json
[
  {
    "response":
    {
      "file":
      {
        "name": "gbk.response",
        "charset": "GBK"
      }
    }
  }
]
```

Charset can also be used in path resource.

* Java API

```java
server.response(pathResource("src/test/resources/gbk.response", Charset.forName("GBK")));
```

* JSON

```json
[
  {
    "response":
    {
      "path_resource":
      {
        "name": "gbk.response",
        "charset": "GBK"
      }
    }
  }
]
```

### Status Code
**@Since 0.7**

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
**@Since 0.7**

By default, response HTTP version is supposed to request HTTP version, but you can set your own HTTP version:

* Java API

```java
server.response(version(HttpProtocolVersion.VERSION_1_0));
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
**@Since 0.7**

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

#### Single URL
**@Since 0.8**

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

#### Failover
**@Since 0.7**

Besides the basic functionality, proxy also support failover, which means if remote server is not available temporarily, the server will know recovery from local configuration.

* Java API

```java
server.request(by("foo")).response(proxy("http://www.github.com", failover("failover.json")));
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
      "proxy" :
        {
          "url" : "http://localhost:12306/unknown",
          "failover" : "failover.json"
        }
    }
}
```

Proxy will save request/response pair into your failover file. If the proxy target is not reachable, proxy will failover from the file. This feature is very useful for development environment, especially for the case the integration server is not stable.

As the file suffix suggests, this failover file is actually a JSON file, which means we can read/edit it to return whatever we want.

#### Playback
**@Since 0.9.1**

Moco also supports playback which also save remote request and response into local file. The difference between failover and playback is that playback only accesses remote server when local request and response are not available.

* Java API

```java
server.request(by("foo")).response(proxy("http://www.github.com", playback("playback.json")));
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
      "proxy" :
        {
          "url" : "http://localhost:12306/unknown",
          "playback" : "playback.json"
        }
    }
}
```

#### Customize Failover/Playback Status
**@Since will be at next release**

You can customize what remote statuses means remote server is not available.

* Java API

```java
server.request(by("foo")).response(proxy("http://www.github.com", failover("failover.json", 400, 500)));
```

* JSON

```json
{
    "request" :
    {
        "text": "foo"
    },
    "response" :
    {
        "proxy" :
        {
            "url" : "http://www.github.com",
            "failover" : {
                "file": "failover.json",
                "status": [404, 500]
            }
        }
    }
}
```

#### Batch URLs
**@Since 0.9.1**

If we want to proxy with a batch of URLs in the same context, proxy can also help us.

* Java API

```java
server.get(match(uri("/proxy/.*"))).response(proxy(from("/proxy").to("http://localhost:12306/target")));
```

* JSON

```json
{
    "request" :
    {
        "uri" : {
            "match" : "/proxy/.*"
        }
    },
    "response" :
    {
        "proxy" : {
            "from" : "/proxy",
            "to" : "http://localhost:12306/target"
        }
    }
}
```

Same with single url, you can also specify a failover.

* Java API

```java
server.request(match(uri("/proxy/.*")))
      .response(proxy("http://localhost:12306/unknown"), failover("failover.response")));
```

* JSON

```json
{
    "request" :
    {
        "uri" : {
            "match" : "/failover/.*"
        }
    },
    "response" :
    {
        "proxy" :
        {
            "from" : "/failover",
            "to" : "http://localhost:12306/unknown",
            "failover" : "failover.response"
        }
    }
}
```

and playback.

* Java API

```java
server.request(match(uri("/proxy/.*")))
      .response(proxy("http://localhost:12306/unknown"), playback("playback.response")));
```

* JSON

```json
{
    "request" :
    {
        "uri" : {
            "match" : "/failover/.*"
        }
    },
    "response" :
    {
        "proxy" :
        {
            "from" : "/failover",
            "to" : "http://localhost:12306/unknown",
            "playback" : "playback.response"
        }
    }
}
```

As you may find, we often set request match same context with response, so Moco gives us a shortcut to do that.

* Java API

```java
server.proxy(from("/proxy").to("http://localhost:12306/target"));
```

* JSON

```json
{
    "proxy" : {
        "from" : "/proxy",
        "to" : "http://localhost:12306/target"
    }
}
```

Same with failover

* Java API
  ```java
  server.proxy(from("/proxy").to("http://localhost:12306/unknown"), failover("failover.response"));
  ```

* JSON
  ```json
  {
    "proxy" :
    {
        "from" : "/failover",
        "to" : "http://localhost:12306/unknown",
        "failover" : "failover.response"
    }
  }
  ```

and playback

* Java API
  ```java
  server.proxy(from("/proxy").to("http://localhost:12306/unknown"), playback("playback.response"));
  ```

* JSON
  ```json
  {
    "proxy" :
    {
        "from" : "/failover",
        "to" : "http://localhost:12306/unknown",
        "playback" : "playback.response"
    }
  }
  ```

### Redirect
**@Since 0.7**

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
**@Since 0.7**

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

#### Cookie Attributes

Cookie attributes are sent in http response, which are used by browsers to determine when to delete a cookie, block a cookie or whether to send a cookie to the server.

##### Path

**@Since 0.11.1**

Path cookie attribute defines the scope of the cookie. You can add your own `path` cookie attribute to your response.

* Java

```java
server.response(cookie("loggedIn", "true", path("/")), status(302));
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
        "login" : {
            "value" : "true",
            "path" : "/"
        }
      }
    }
}
```

##### Domain

**@Since 0.11.1**

Domain cookie attribute defines the scope of the cookie. You can add your own `domain` cookie attribute to your response.

* Java

```java
server.response(cookie("loggedIn", "true", domain("github.com")), status(302));
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
        "login" : {
            "value" : "true",
            "domain" : "github.com"
        }
      }
    }
}
```

##### Secure

**@Since 0.11.1**

A secure cookie can only be transmitted over an encrypted connection. You can add your own `secure` cookie attribute to your response.

* Java

```java
server.response(cookie("loggedIn", "true", secure()), status(302));
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
        "login" : {
            "value" : "true",
            "secure" : "true"
        }
      }
    }
}
```

##### HTTP Only

**@Since 0.11.1**

An http only cookie cannot be accessed by client-side APIs. You can add your own `httpOnly` cookie attribute to your response.

* Java

```java
server.response(cookie("loggedIn", "true", httpOnly()), status(302));
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
        "login" : {
            "value" : "true",
            "httpOnly" : "true"
        }
      }
    }
}
```

##### Max Age

**@Since 0.11.1**

The Max-Age attribute can be used to set the cookie's expiration as an interval of seconds in the future, relative to the time the browser received the cookie. You can add your own `maxAge` cookie attribute to your response.

* Java

```java
server.response(cookie("loggedIn", "true", maxAge(1, TimeUnit.HOURS)), status(302))
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
        "login" : {
            "value" : "true",
             "maxAge": {
                 "duration": 1,
                 "unit": "hour"
             }
        }
      }
    }
}
```

### Attachment
**@Since 0.10.0**

Attachment is often used in web development. You can setup an attachment in Moco as following. As you will see, you'd better set a filename for client to receive.

* Java API

```java
server.get(by(uri("/"))).response(attachment("foo.txt", file("foo.response")));
```

* JSON

```json
{
  "request": {
    "uri": "/file_attachment"
  },
  "response": {
    "attachment": {
        "filename": "foo.txt",
        "file": "foo.response"
    }
  }
}
```

### Latency
**@Since 0.7**

Sometimes, we need a latency to simulate slow server side operation.

**@Since 0.10.1**

It's easy to setup latency with time unit.

* Java API

```java
server.response(latency(1, TimeUnit.SECONDS));
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
      "latency":
        {
          "duration": 1,
          "unit": "second"
        }
    }
}
```

The original API without time unit introduced in 0.7 has been deprecated.

### Sequence
**@Since 0.7**

Sometimes, we want to simulate a real-world operation which change server side resource. For example:

* First time you request a resource and "foo" is returned
* We update this resource
* Again request the same URL, updated content, e.g. "bar" is expected.

We can do that by

```java
server.request(by(uri("/seq"))).response(seq("foo", "bar", "blah"));
```

The other response settings are able to be set as well.

```java
server.request(by(uri("/seq"))).response(seq(status(302), status(302), status(200)));
```

**@Since 0.12.0**

```json
{
    "request" : {
      "uri" : "/seq"
    },
    "response": {
      "seq": [
          {
            "text" : "foo"
          },
          {
            "text" : "bar"
          },
          {
            "text" : "blah"
          }
      ]
    }
}
```

The other response settings are able to be set for json as well.

```json
{
    "request" : {
      "uri" : "/seq"
    },
    "response": {
      "seq": [
          {
            "status" : "302"
          },
          {
            "status" : "302"
          },
          {
            "status" : "200"
          }
      ]
    }
}
```

### Cycle
**@Since will be at next release**

Cycle is similar to `seq`, but it will return response as cycle. An example is as following:

```java
server.request(by(uri("/cycle"))).response(cycle("foo", "bar", "blah"));
```

The response will returned as cycle:
* foo
* bar
* blah
* foo
* bar
* blah
* ...

The other response settings are able to be set as well.

```java
server.request(by(uri("/cycle"))).response(cycle(status(302), status(302), status(200)));
```

**@Since 0.12.0**

```json
{
    "request" : {
      "uri" : "/cycle"
    },
    "response": {
      "cycle": [
          {
            "text" : "foo"
          },
          {
            "text" : "bar"
          },
          {
            "text" : "blah"
          }
      ]
    }
}
```

The other response settings are able to be set for json as well.

```json
{
    "request" : {
      "uri" : "/cycle"
    },
    "response": {
      "cycle": [
          {
            "status" : "302"
          },
          {
            "status" : "302"
          },
          {
            "status" : "200"
          }
      ]
    }
}
```

### JSON Response
If the response is JSON, we don't need to write JSON text with escape character in code.

**@Since 0.10.2**

You can give a POJO to Java API, it will be converted JSON text. Hint, this api will setup Content-Type header as well.

```java
server.request(by(uri("/json"))).response(toJson(pojo));
```

**@Since 0.12.0**
`toJson` will be removed from 0.12.0, use `json` instead.

```java
server.request(by(uri("/json"))).response(json(pojo));
```

Note that this functionality is implemented in Jackson, please make sure your POJO is written in Jackson acceptable format. 

**@Since 0.9.2**

For JSON API, just give json object directly

```json
{
    "request": 
      {
        "uri": "/json"
      },
    "response": 
      {
        "json": 
          {
            "foo" : "bar"
          }
      }
}
```

## Mount
**@Since 0.7**

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

Glob is acceptable to filter specified files, e.g we can include by

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

**@Since 0.10.1**
You can also specify some response information like normal response, e.g.

* JSON

```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri",
      "headers" : {
        "Content-Type" : "text/plain"
      }
    }
}
```

## Template(Beta)
**Note**: Template is an experimental feature which could be changed a lot in the future. Feel free to tell how it helps or you need more features in template.

Sometimes, we need to customize our response based on something, e.g. response should have same header with request.

The goal can be reached by template:

### Request

You can get request information with `req` in template.

#### Version
**@Since 0.8**

With `req.version`, request version can be retrieved in template.

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

#### Method
**@Since 0.8**

Request method is identified by `req.method`.

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

#### Content
**@Since 0.8**

All request content can be used in template with `req.content`

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

#### Header
**@Since 0.8**

Header is another important element in template and we can use `req.headers` for headers.

* Java

```java
server.request(by(uri("/template"))).response(template("${req.headers['foo']}"));
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

#### Query
**@Since 0.8**

`req.queries` helps us to extract request query.

* Java

```java
server.request(by(uri("/template"))).response(template("${req.queries['foo']}"));
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

#### Form
**@Since 0.9.1**

`req.forms` can extract form value from request.

* Java

```java
server.request(by(uri("/template"))).response(template("${req.forms['foo']}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.forms['foo']}"
        }
    }
}
```

#### Cookie
**@Since 0.9.1**

Cookie from request can extracted by `req.cookies`.

* Java

```java
server.request(by(uri("/template"))).response(template("${req.cookies['foo']}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.cookies['foo']}"
        }
    }
}
```

#### JSON
**@Since will be at next release**

If your request is a JSON request, you can use `req.json` to visit your json object.

* Java

```java
server.request(by(uri("/template"))).response(template("${req.json.foo}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${req.json.foo}"
        }
    }
}
```


### Custom Variable
**@Since 0.9.1**

You can provide your own variables in your template.

* Java

```java
server.request(by(uri("/template"))).response(template("${foo}", "foo", "bar"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": {
                "with" : "${foo}",
                "vars" : {
                    "foo" : "bar"
                }
            }
        }
    }
}
```

**@Since 0.10.0**

You can also use extractor to extract information from request.

* Java

```java
server.request(by(uri("/template"))).response(template("${foo}", "foo", jsonPath("$.book[*].price")));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": {
                "with" : "${foo}",
                "vars" : {
                    "foo" : {
                      "json_path": "$.book[*].price"
                    }
                }
            }
        }
    }
}
```

Other extractors, e.g. xpath also work here.

### Template Function

#### now

**@Since will be at next release**

Current time can retrieved by 'now' function and a date format string should be passed as argument.

* Java

```java
server.request(by(uri("/template"))).response(template("${now('yyyy-MM-dd')}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${now(\"yyyy-MM-dd\")}"
        }
    }
}
```

#### random

**@Since will be at next release**

`random` will generate a random number. If you didn't pass any argument, the generated random will be between 0 and 1.

* Java

```java
server.request(by(uri("/template"))).response(template("${random()}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${random()}"
        }
    }
}
```

The first argument is random number range which means the generated number will be between 0 and range.

* Java

```java
server.request(by(uri("/template"))).response(template("${random(100)}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${random(100)}"
        }
    }
}
```

The last argument is number format.

* Java

```java
server.request(by(uri("/template"))).response(template("${random(100, '###.###')}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${random(100, \"###.###\")}"
        }
    }
}
```

You can also use number format directly without range.

* Java

```java
server.request(by(uri("/template"))).response(template("${random('###.###')}"));
```

* JSON

```json
{
    "request": {
        "uri": "/template"
    },
    "response": {
        "text": {
            "template": "${random(\"###.###\")}"
        }
    }
}
```

### Redirect
**@Since 0.10.2**

Redirect can also be set as template.

* Java

```java
server.request(by(uri("/redirectTemplate"))).redirectTo(template("${var}", "var", ""https://github.com"));
```

* Json

```json
{
      "request" :
      {
          "uri" : "/redirect-with-template"
      },

      "redirectTo" : {
          "template" : {
              "with" : "${url}",
              "vars" : {
                  "url" : "https://github.com"
              }
          }
      }
  }
```

### File Name Template
**@Since 0.10.1**

Template can also be used in file name, thus response can be different based on different request.

* Java

```java
server.response(file(template("${req.headers['foo'].txt")));
```

* JSON

```json
[
  {
    "response": {
      "file": {
        "name": {
          "template": "${req.headers['foo'].txt"}"
        }
      }
    }
  }
]
```

### Proxy
**@Since 0.11.1**

You can use template in proxy API, so that you can dynamically decide which URL you will forward the request to.

* Java

```java
server.request(by(uri("/proxy"))).response(proxy(template("http://localhost:12306/${req.queries['foo']}")))
```

* JSON

```json
{
    "request" : {
        "uri" : "/proxy"
    },
    "response" : {
        "proxy" : {
            "url" : {
                "template": "http://localhost:12306/${req.queries['foo']}"
            }
        }
    }
}
```

### Template for Event Action

Template also can ben applied to event action. Check out [Event](#event) for more details about event.

* Java

```java
server.request(by(uri("/event"))).response("event").on(complete(post("http://localhost:12306/target"), template("${target}", of("target", var("target"))))));
```

* JSON

```json
{
  "request": {
    "uri": "/event"
  },
  "response": {
    "text": "event"
  },
  "on": {
    "complete": {
      "post": {
        "url": "http://localhost:12306/target",
        "content": {
          "template": {
            "with": "${target}",
            "vars": {
              "target" : "target"
            }
          }
        }
      }
    }
  }
}
```

## Event
You may need to request another site when you receive a request, e.g. OAuth. Event could be your helper at that time.

### Complete
**@Since 0.9**

Complete event will be fired after your request has been handled completely.

#### Get Request

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

#### Post Request

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

**@Since 0.12.0**

If your post content is JSON, you can use `json` in your configuration directly.

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
                "json": {
                    "foo" : "bar"
                }
            }
        }
    }
}
```

Let me know if you need more methods.

### Asynchronous
**@Since 0.9**

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

## Verify
**@Since 0.9**

Someone may want to verify what kind of request has been sent to server in testing framework.

You can verify request like this:

```java
RequestHit hit = requestHit();
final HttpServer server = httpServer(12306, hit);
server.get(by(uri("/foo"))).response("bar");

running(server, new Runnable() {
  @Override
  public void run() throws Exception {
    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
  }
});

hit.verify(by(uri("/foo"))), times(1));
```

You can also verify unexpected request like this:
```java
hit.verify(unexpected(), never());
```

Many verification can be used:
* **never**: none of this kind of request has been sent.
* **once**: only once this kind of request has been sent.
* **time**: how many times this kind of request has been sent.
* **atLeast**: at least how many time this kind of request has been sent.
* **atMost**: at least how many time this kind of request has been sent.
* **between**: the times this kind of request has been sent should be between min and max times.

## Miscellaneous
### Port
**@Since 0.9**

If you specify a port for your stub server, it means the port must be available when you start server. This is not case sometimes.

Moco provides you another way to start your server: specify no port, and it will look up an available port. The port can be got by port() method. The example is as follow:

```java
final HttpServer server = httpServer();
server.response("foo");

running(server, new Runnable() {
  @Override
  public void run() throws Exception {
    Content content = Request.Get("http://localhost:" + server.port()).execute().returnContent();
    assertThat(content.asString(), is("foo"));
  }
});
```

The port will be returned only when server is started, otherwise the exception will be thrown.

For standalone server, if you need this behaviour, simply don't give port argument.

```shell
java -jar moco-runner-<version>-standalone.jar start -c foo.json
```

The port information will shown on screen.

### Log
**@Since 0.9.1**

If you want to know more about how your Moco server running, log will be your helper.

```java
final HttpServer server = httpServer(log());
```

The Moco server will log all your requests and responses in your console.

It you want to keep log, you can use log interface as following:

```java
final HttpServer server = httpServer(log("path/to/log.log"));
```

**@Since 0.10.1**

Log content may contain some non UTF-8 character, charset could be specified in log API:

```
final HttpServer server = httpServer(log("path/to/log.log", Charset.forName("GBK")));
```

The log will be saved in your log file.

#### Log with verifier

Log will help you for some legacy system to know what detailed request/response looks like. You also need to do some verification work. Here is the case.

```java
RequestHit hit = requestHit();
final HttpServer server = httpServer(port(), hit, log());
```