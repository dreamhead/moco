Moco is a easy setup stub framework, mainly focusing on testing and integration, inspired by Mock framework, e.g. [Mockito](http://code.google.com/p/mockito/), and [Playframework](http://www.playframework.org/)

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
    "com.github.dreamhead:moco-core:0.5-SNAPSHOT",
    "com.github.dreamhead:moco-runner:0.5-SNAPSHOT"
  )
}
```

Moco can be used as standalone to run with configuration and you can download standalone directly:
[Standalone Moco Runner](https://oss.sonatype.org/content/groups/public/com/github/dreamhead/moco-runner/0.5-SNAPSHOT/moco-runner-0.5-20121212.112753-2-standalone.jar)

# Usage
You have two ways to use Moco. One is API, which you can use in your unit test. The other is that run Moco as standalone. Currently, you put all your configuration in JSON file.

## API
Here is an typical Moco test case in JUnit.

```java
@Test
public void should_response_as_expected() {
  MocoHttpServer server = httpserver(8080);
  server.reponse("foo");

  running(server, new Runnable() {
    @Override
    public void run() {
      try {
        Content content = Request.Get("http://localhost:8080").execute().returnContent();
        assertThat(content.asString(), is("foo"));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
```

As shown above, we created a new server and configure it as expected. And then run our test against this server.

Here, We use [Apache Http Client Fluent API](http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html) to request our testing server.

## Standalone
Now we are going to run Moco as standalone server. First of all, a JSON configuration file needs to be provided to start Moco.

```json
{
  "port" : 8080,
  "sessions" :
    [
      {
        "response" :
          {
            "text" : "foo"
          }
      }
    ]
}
```

It's time to run Moco standalone server:

```shell
java -jar moco-runner-<version>-standalone.jar foo.json
```

Now, open your browser and input "http://localhost:8080". You will see "foo". That's it.

# Configurations
Moco mainly focuses on server configuration. There are only two kinds of configuration right now: Request and Response.

That means if we get the expected request and then return our response. You have seen the simplest test case in previous example, that is, no matter what request is, return "foo" as response.

Now, you can see a Moco reference in details.

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

You will focus HTTP header at times:

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

### XPath

XML/HTML is popular format for HTTP server. Moco allows us to match request with XPath.

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
Moco also supports HTTP status codein response.

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
      "headers" : {
        "content-type" : "application/json"
      }
    }
}
```

### Url

You can also response with the specified url, just like a proxy.

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

### Sequence

Sometimes, you want to simulate a real-world operation which change server side resource. For example:
* First time you request a resource and "foo" is returned
* You update this resource
* Again request the same URL, updated content, e.g. "bar" is expected.

You can do that by
```java
server.request(by(uri("/foo"))).response(seq("foo", "bar", "blah"));
```