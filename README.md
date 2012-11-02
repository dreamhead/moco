Moco is a testing server framework, inspired by Mock framework, e.g. [Mockito](http://code.google.com/p/mockito/), and [Playframework](http://www.playframework.org/)

#  How to use it?

Before we start, we should have a server.

```
MocoHttpServer server = httpserver(8080);
```

## Response as expected

In most our test cases, what we expect is just some specific content. You can setup with Moco like this.

```java
server.reponse("foo");
```

Now we can start our test as following:

```java
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
});
```

Here, We use [Apache Http Client Fluent API](http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html) to request our testing server.

# Expect request

Only response is not sufficient. In most cases, you also have expectation for request.

## expect content

If you want to response according to request content, Moco server can be configured as following:

```java
server.request(eq("foo")).response("bar");
```

## expect URI

If request uri is your major focus, Moco server could be like this:

```java
server.request(eq(uri("/foo"))).response("bar");

```

# Request/Response from File

In the real world, the request/response content is much bigger for String in code. It would be better to load content from File.

```java
InputStream requestIs = this.getClass().getClassLoader().getResourceAsStream("foo.request");
InputStream responseIs = this.getClass().getClassLoader().getResourceAsStream("foo.response");

server.request(eq(stream(requestIs))).response(stream(responseIs));
```

# Response with sequence

Sometimes, you want to simulate a real-world operation which change server side resource. For example:
* First time you request a resource and "foo" is returned
* You update this resource
* Again request the same URL, updated content, e.g. "bar" is expected.

You can do that by
```java
server.request(eq(uri("/foo"))).response(seq("foo", "bar", "blah"));
```

