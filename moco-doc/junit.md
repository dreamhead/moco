# JUnit Integration

Moco makes use of Test Rule in JUnit to simplify JUnit integration. **MocoJunitRunner** provides several ways to run Moco server as Test Rule, which can start Moco server before your test and stop after the test.

Table of Contents
=================

* [HTTP Server](#http-server)
  * [POJO HTTP Server](#pojo-http-server)
  * [JSON HTTP Server](#json-http-server)
* [Socket Server](#socket-server)
  * [POJO Socket Server](#pojo-socket-server)
  * [JSON Socket Server](#json-socket-server)
* [Rest Server](#rest-server)
  * [POJO Rest Server](#pojo-rest-server)
  * [JSON Rest Server](#json-rest-server)

## HTTP Server

### POJO HTTP Server

**@Since 0.11.0**

**httpRunner** can reference a HttpServer object.

```java
public class MocoJunitPojoHttpRunnerTest {
  private static HttpServer server;

  static {
    server = httpServer(12306);
    server.response("foo");
  }

  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.httpRunner(server);
  
  ...
}
```

### JSON HTTP Server

**@Since 0.11.0**

**jsonHttpRunner** can reference a JSON file as a HTTP server.

```java
public class MocoJunitJsonHttpRunnerTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonHttpRunner(12306, "foo.json");
  
  ...
}
```

**@Since 0.11.1**

JSON configuration can be retrieved from the classpath.

```java
public class MocoJunitJsonHttpRunnerTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonHttpRunner(12306, Moco.pathResource("foo.json"));

  ...
}
```

## HTTPS Server

### POJO HTTPS Server

**@Since 0.11.1**

**httpsRunner** can reference a HttpsServer object.

```java
public class MocoJunitPojoHttpRunnerTest {
  private static final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");
  private static HttpServer server;

  static {
    server = httpsServer(12306, DEFAULT_CERTIFICATE);
    server.response("foo");
  }

  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.httpsRunner(server);

  ...
}
```

### JSON HTTPS Server

**@Since 0.11.1**

**jsonHttpsRunner** can reference a JSON file as a HTTP server.

```java
public class MocoJunitJsonHttpRunnerTest {
  private static final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonHttpsRunner(12306, "foo.json", DEFAULT_CERTIFICATE);

  ...
}
```

**@Since 0.11.1**

JSON configuration can be retrieved from the classpath.

```java
public class MocoJunitJsonHttpRunnerTest {
  private static final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonHttpsRunner(12306, Moco.pathResource("foo.json"), DEFAULT_CERTIFICATE);

  ...
}
```

## Socket Server

### POJO Socket Server

**@Since 0.11.0**

**socketRunner** can reference a SocketServer object.

```java
public class MocoJunitPojoSocketRunnerTest {
  private static SocketServer server;

  static {
    server = socketServer(12306);
    server.response("bar\n");
  }

  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.socketRunner(server);
  
  ...
}
```

### JSON Socket Server

**@Since 0.11.0**

**jsonHttpRunner** can reference a JSON file as a Socket server.

```java
public class MocoJunitJsonSocketRunnerTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonSocketRunner(12306, "foo.json");

  ...
}
```

**@Since 0.11.1**

JSON configuration can be retrieved from the classpath.

```java
public class MocoJunitJsonHttpRunnerTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonSocketRunner(12306, Moco.pathResource("foo.json"));

  ...
}
```

## Rest Server

### POJO Rest Server

**@Since 0.11.0**

**restRunner** can reference a RestServer object.

```java
public class MocoJunitPojoRestRunnerTest {
  private static RestServer server;

  static {
    server = restServer(12306);
    server.resource("targets",
      post().response(status(201), header("Location", "/targets/123"))
    );
  }

  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.restRunner(server);

  ...
}
```

### JSON Rest Server

**@Since 0.11.1**

**jsonRestRunner** can reference a JSON file as a HTTP server.

```java
public class MocoJunitJsonRestRunnerTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonRestRunner(12306, "rest.json");

  ...
}
```

**@Since 0.11.1**

JSON configuration can be retrieved from the classpath.

```java
public class MocoJunitJsonRestRunnerTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonRestRunner(12306, Moco.pathResource("foo.json"));

  ...
}
```