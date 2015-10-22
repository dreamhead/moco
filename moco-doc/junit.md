# JUnit Integration

Moco make use of Test Rule in JUnit to simplify JUnit integration. **MocoJunitRunner** provices several ways to run Moco server as Test Rule, which can start Moco server before your test and stop after the test.

**Table of Contents**

* [HTTP Server](#http-server)
  * [POJO HTTP Server](#pojo-http-server)
  * [JSON HTTP Server](#json-http-server)
* [Socket Server](#socket-server)
  * [POJO Socket Server](#pojo-socket-server)
  * [JSON Socket Server](#json-socket-server)

## HTTP Server

### POJO HTTP Server

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

**jsonHttpRunner** can reference a JSON file as a HTTP server.

```java
public class MocoJunitJsonHttpRunnerTest extends AbstractMocoStandaloneTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonHttpRunner(12306, "foo.json");
  
  ...
}
```

## Socket Server

### POJO Socket Server

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

**jsonHttpRunner** can reference a JSON file as a Socket server.

```java
public class MocoJunitJsonSocketRunnerTest {
  @Rule
  public MocoJunitRunner runner = MocoJunitRunner.jsonSocketRunner(12306, "foo.json");

  ...
}
```
