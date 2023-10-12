# JUnit 5 Integration
**@Since will be at next release**

Moco makes use of Extension in JUnit 5 to simplify JUnit integration.

@MocoExtension is required to use Moco in JUnit 5. Every test class use Moco should be annotated with @ExtendWith(MocoExtension.class).

```java
@ExtendWith(MocoExtension.class)
class WithMocoTest {
  ...
}
```

With @MocoExtesion, Moco server will be started before test and stopped after test.

## HTTP Server

@MocoHttpServer could be used to start a HTTP server. Configuration file and port are required.

```java
@ExtendWith(MocoExtension.class)
@MocoHttpServer(filepath = "foo/foo.json", port=12306)
public class MocoHttpTest {
  @Test
  public void should_return_expected_message() throws IOException {
    Content content = Request.Get("http://localhost:12306").execute().returnContent();
    assertThat(content.asString(), is("foo"));
  }  
}
```

**filepath** is provided here, you can also use `classpath` to retrieve configuration file from classpath.

```java
@ExtendWith(MocoExtension.class)
@MocoHttpServer(classpath = "foo.json", port=12306)
public class MocoHttpTest {
  ...
}
```

## HTTPS Server

Besides @MocoHttpServer, you should also use @MocoCertificate to start Moco as a HTTPS server.

```java
@ExtendWith(MocoExtension.class)
@MocoHttpServer(filepath = "foo/foo.json", port=12306)
@MocoCertificate(filepath = "certificate/cert.jks", keyStorePassword = "mocohttps", certPassword = "mocohttps")
public class MocoHttpTest {
  @Test
  public void should_return_expected_message() throws IOException {
    Content content = Request.Get("http://localhost:12306").execute().returnContent();
    assertThat(content.asString(), is("foo"));
  }  
}
```

Certificate file, keyStorePassword and certPassword can be configured with @MocoCertificate.

You can also configure certificate file with `classpath`.

```java
@ExtendWith(MocoExtension.class)
@MocoHttpServer(classpath = "foo.json", port=12306)
@MocoCertificate(classpath = "certificate/cert.jks", keyStorePassword = "mocohttps", certPassword = "mocohttps")
public class MocoHttpTest {
  @Test
  public void should_return_expected_message() throws IOException {
    ...
  }  
}
```


