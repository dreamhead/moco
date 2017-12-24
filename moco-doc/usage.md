# Usage

You have several ways to use Moco. One is API, which you can use in your unit test. The other is that run Moco as standalone. Currently, you put all your configuration in JSON file.

On the other hand, Moco has several different ways to integrate with some tools: Maven plugin, Gradle plugin and shell support.

Table of Contents
=================

* [API](#api)
  * [dependency](#dependency)
  * [API example](#api-example)
    * [Runner API](#runner-api)
* [Standalone](#standalone)
* [JSON configuration in Java API](#json-configuration-in-java-api)
* [HTTPS](#https)
* [Socket](#socket)
* [JUnit Integration](#junit-integration)
* [Maven Plugin](#maven-plugin)
* [Gradle Plugin](#gradle-plugin)
* [Shell](#shell)
* [Scala](#scala)

## API

### dependency

Moco has been published on Maven repository, so you can refer to it directly in your dependency. This is core dependency

```xml
<dependency>
  <groupId>com.github.dreamhead</groupId>
  <artifactId>moco-core</artifactId>
  <version>0.12.0</version>
</dependency>
```

A gradle example is as follow:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  testCompile(
    "com.github.dreamhead:moco-core:0.12.0",
  )
}
```

### API example

Here is an typical Moco test case in JUnit.

```java
import org.junit.Test;
import java.io.IOException;
import com.github.dreamhead.moco.HttpServer;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import com.github.dreamhead.moco.Runnable;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Test
public void should_response_as_expected() throws Exception {
    HttpServer server = httpServer(12306);
    server.response("foo");

    running(server, new Runnable() {
        @Override
        public void run() throws IOException {
            Content content = Request.Get("http://localhost:12306").execute().returnContent();
            assertThat(content.asString(), is("foo"));
        }
    });
}

```

As shown above, we created a new server and configure it as expected. And then run our test against this server.

Here, We use [Apache Http Client Fluent API](http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html) to request our testing server.

#### Runner API
You may need to control server start/stop by yourself, for example, in your Before (or setup) method, runner API could be used.

```java
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Runner.runner;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRunnerTest {
    private Runner runner;

    @Before
    public void setup() {
        HttpServer server = httpServer(12306);
        server.response("foo");
        runner = runner(server);
        runner.start();
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void should_response_as_expected() throws IOException {
        Content content = Request.Get("http://localhost:12306").execute().returnContent();
        assertThat(content.asString(), is("foo"));
    }
}
```

## Standalone

Moco can be used as standalone to run with configuration and you can download standalone directly:
[Standalone Moco Runner](http://central.maven.org/maven2/com/github/dreamhead/moco-runner/0.12.0/moco-runner-0.12.0-standalone.jar)

First of all, a JSON configuration file needs to be provided to start Moco.

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
(foo.json)

It's time to run Moco standalone server:

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -c foo.json
```

Now, open your browser and input "http://localhost:12306". You will see "foo". That's it.

## JSON configuration in Java API

**@Since 0.10.0**

If you have setup your server with JSON configuration, you can also your configuration from Java API.

```java
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonHttpServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJsonHttpRunnerTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response() throws Exception {
        final HttpServer server = jsonHttpServer(12306, file("foo.json"));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Content content = Request.Get("http://localhost:12306").execute().returnContent();
                assertThat(content.asString(), is("foo"));
            }
        });
    }
}
```

## HTTPS

HTTPS is also a mainstream usage for HTTP protocol. Moco supports HTTPS as well. The main difference in API is that a certificate is required for HTTPS.
On the other hand, **httpsServer** should be used.

```java
final HttpsCertificate certificate = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");
final HttpsServer server = httpsServer(certificate, hit);
```

If you want to use HTTPS for standalone server. certificate information could be provided as CLI arguments.

```shell
java -jar moco-runner-<version>-standalone.jar https -p 12306 -c foo.json --https /path/to/cert.jks --cert mocohttps --keystore mocohttps
```

## Socket

Socket is a common integration channel. There is only content available in socket.

```java
final SocketServer server = socketServer(12306);
```

You can also use socket in standalone server.

```shell
java -jar moco-runner-<version>-standalone.jar socket -p 12306 -c foo.json
```

More socket APIs can be found [here](/moco-doc/socket-apis.md).

## JUnit Integration

**@Since 0.11.0**

Moco provides JUnit integration to simplify the use of Moco in JUnit.
 
More details can be found in [here](/moco-doc/junit.md).

## Maven Plugin

Moco also can be used as Maven plugin.

https://github.com/GarrettHeel/moco-maven-plugin

## Gradle Plugin

Moco can be used in Gradle

https://github.com/silverjava/moco-gradle-plugin

## Shell

If you are using Mac or Linux, you may try the following approach:

* Make sure you have JDK 6 or later.
* [Download the script](/moco-shell/moco?raw=true).
* Place it on your $PATH. (~/bin is a good choice if it is on your path.)
* Set it to be executable. (chmod 755 ~/bin/moco)

Now, you can try

```shell
moco http -p 12306 -c foo.json
```

It will download the latest moco automatically if you don't have locally.

## Scala

Scala fans can find Scala wrapper in

https://github.com/treppo/moco-scala
