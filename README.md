[![Build Status](https://travis-ci.org/dreamhead/moco.png?branch=master)](https://travis-ci.org/dreamhead/moco)

Moco is an easy setup stub framework, mainly focusing on testing and integration, inspired by Mock framework, e.g. [Mockito](http://code.google.com/p/mockito/), and [Playframework](http://www.playframework.com/)

# Why
Integration, especially based on HTTP protocol, e.g. web service, REST etc, is wildly used in most our development.

In the old days, we just deployed another WAR to an application server, e.g. Jetty or Tomcat etc. As we all konw, it's so boring to develop a WAR and deploy it to any application server, even if we use embeded server. And the WAR needs to be reassembled even if we just want to change a little bit.

# Dependencies
Moco has been published on Maven repository, so you can refer it directly in your code.

A gradle example is as follow:
```groovy
repositories {
  maven {
    url "https://oss.sonatype.org/content/groups/public"
  }
  mavenCentral()
}

dependencies {
  compile(
    "com.github.dreamhead:moco-core:0.6-SNAPSHOT",
    "com.github.dreamhead:moco-runner:0.6-SNAPSHOT"
  )
}
```

Moco can be used as standalone to run with configuration and you can download standalone directly:
[Standalone Moco Runner](https://oss.sonatype.org/content/groups/public/com/github/dreamhead/moco-runner/0.6.3-SNAPSHOT/moco-runner-0.6.3-20130314.090338-1-standalone.jar)

# Usage
You have two ways to use Moco. One is API, which you can use in your unit test. The other is that run Moco as standalone. Currently, you put all your configuration in JSON file.

## API
Here is an typical Moco test case in JUnit.

```java
@Test
public void should_response_as_expected() {
  MocoHttpServer server = httpserver(12306);
  server.reponse("foo");

  running(server, new Runnable() {
    @Override
    public void run() throws IOException {
      Content content = Request.Get("http://localhost:12306").execute().returnContent();
      assertThat(content.asString(), is("foo"));
    }
  }
}
```

As shown above, we created a new server and configure it as expected. And then run our test against this server.

Here, We use [Apache Http Client Fluent API](http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html) to request our testing server.

## Standalone
Now we are going to run Moco as standalone server. First of all, a JSON configuration file needs to be provided to start Moco.

```json
[
  {
    "response" :
      {
        "text" : "foo"
      }
  }
]
```

It's time to run Moco standalone server:

```shell
java -jar moco-runner-<version>-standalone.jar -p 12306 foo.json
```

Now, open your browser and input "http://localhost:12306". You will see "foo". That's it.

# Configurations
Moco mainly focuses on server configuration. There are only two kinds of configuration right now: Request and Response.

That means if we get the expected request and then return our response. You have seen the simplest test case in previous example, that is, no matter what request is, return "foo" as response.

Now, you can see a Moco reference in details.

**WARNING** the json configuration below is just configuration snippet for one pair of request and response, instead of the whole configuration file.

## Request

### Content
If you want to response according to request content, Moco server can be configured as following:

* API
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

* API
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
### XML Request
* API
```java
server.request(xml(text("<request><parameters><id>1</id></parameters></request>"))).response("foo");
```
* JSON
```json
{
   "request": {
    "uri": "/xml",
    "text": {
      "xml": "<request><parameters><id>1</id></parameters></request>"
    }
  },
  "response": {
    "text": "foo"
  }
}
```
Or
```json
{
   "request": {
    "uri": "/xml",
    "file": {
      "xml": "your_file.xml"
    }
  },
  "response": {
    "text": "foo"
  }
}
```
### JSON Request
* API
```java
server.request(json(text("{"foo":"bar"}"))).response("foo");
```
* JSON
```json
{
  "request": {
    "uri": "/json",
    "text": {
      "json": "{"foo":"bar"}"
    }
  },
  "response": {
    "text": "foo"
  }
}
```
Or
```json
{
  "request": {
    "uri": "/json",
    "file": {
      "json": "your_file.json"
    }
  },
  "response": {
    "text": "foo"
  }
}
```
### URI
If request uri is your major focus, Moco server could be like this:

* API
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
Sometimes, your request has parameters:

* API
```java
server.request(and(by(uri("/foo")), eq(query("param"), "blah"))).response("bar")
```

* JSON
```json
{
  "request" :
    {
      "uri" : "/foo"
      "queries" : {
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

* API
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

* API
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

### Header

We will focus HTTP header at times:

* API
```java
server.request(eq(header("foo"), "bar")).response("blah")
```

* JSON
```json
{
  "request" :
    {
      "method" : "post",
      "headers" : {
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

* API
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

### XML Request
XML is popular format for Web Service. When request is XML, in the most case, only XML structure is important, no one really care about whilespace, tab etc. xml operation can be used for this case.

* API
```java
server.request(xml(text("<request><parameters><id>1</id></parameters></request>"))).response("foo");
```

* JSON
```json
{
   "request": {
    "uri": "/xml",
    "text": {
      "xml": "<request><parameters><id>1</id></parameters></request>"
    }
  },
  "response": {
    "text": "foo"
  }
}
```

**NOTE**: Please escape the quote in text.

The large request can be put into a file:

```json
{
   "request": {
    "uri": "/xml",
    "file": {
      "xml": "your_file.xml"
    }
  },
  "response": {
    "text": "foo"
  }
}
```

### XPath

For the XML/HTML request, Moco allows us to match request with XPath.

* API
```java
server.request(eq(xpath("/request/parameters/id/text()"), "1")).response("bar");
```

* JSON
```json
{
  "request" :
    {
      "method" : "post",
      "xpaths" : {
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
Json is rising with RESTful style architecture. Just like XML, in the most case, only JSON structure is important, so json operator can be used.

* API
```java
server.request(json(text("{"foo":"bar"}"))).response("foo");
```

* JSON
```json
{
  "request": {
    "uri": "/json",
    "text": {
      "json": "{\"foo\":\"bar\"}"
    }
  },
  "response": {
    "text": "foo"
  }
}
```

**NOTE**: Please escape the quote in text.

The large request can be put into a file:

```json
{
  "request": {
    "uri": "/json",
    "file": {
      "json": "your_file.json"
    }
  },
  "response": {
    "text": "foo"
  }
}
```

## Response

### Content

As you have seen in previous example, response with content is pretty easy.

* API
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

* API
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

* API
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

### Header
We can also specify HTTP header in response.

* API
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

### Url

We can also response with the specified url, just like a proxy.

* API
```java
server.request(by("foo")).response(url("http://www.github.com"));
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
      "url" : "http://www.github.com"
    }
}
```


### Redirect

Redirect is a common case for normal web development. We can simply redirect a request to different url.

* API
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

* API
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

* API
```java
server.request(by("foo")).response(latency(5000));
```

* JSON
```json
[
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
]
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

### Mount

Moco allows us to mount a directory to uri.

* API
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

* API
```java
server.mount(dir, to("/uri"), include("*.txt"));
```

* JSON
```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri"
      "includes" :
      [
        "*.txt"
      ]
    }
}
```

or exclude by

* API
```java
server.mount(dir, to("/uri"), exclude("*.txt"));
```

* JSON
```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri"
      "excludes" :
      [
        "*.txt"
      ]
    }
}
```

even compose them by

* API
```java
server.mount(dir, to("/uri"), include("a.txt"), exclude("b.txt"), include("c.txt"));
```

* JSON
```json
{
  "mount" :
    {
      "dir" : "dir",
      "uri" : "/uri"
      "includes" :
      [
        "a.txt",
        "b.txt"
      ],
      "excludes" :
      [
        "c.txt"
      ]
    }
}
```

### Cache
Moco supports cache, which means you can cache resource access, so no need to access resource many times.

* API
```java
server.response(cache(file("target.txt")));
```

* JSON
```json
{
  "response" :
  {
    "cache" :
    {
      "file" : "cache.response"
    }
  }
}
```

You can even persist your cache
* API
```java
server.response(cache(file("target.txt"), with(file("persist.txt"))));
```

* JSON
```json
{
  "response" :
  {
    "cache" :
    {
      "file" : "cache.response",
      "with" :
      {
        "file" : "src/test/resources/cache/cache.persistence"
      }
    }
  }
}
```

**TIPS** even if the resource is not accessible, response can still be returned from persistence file.