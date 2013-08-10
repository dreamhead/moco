# Usage
You have several ways to use Moco. One is API, which you can use in your unit test. The other is that run Moco as standalone. Currently, you put all your configuration in JSON file.

On the other hand, Moco has serveral different ways to integrate with some tools: Maven plugin, Gradle plugin and shell support.

## API

### dependency

Moco has been published on Maven repository, so you can refer to it directly in your dependency. This is core dependency

```xml
<dependency>
  <groupId>com.github.dreamhead</groupId>
  <artifactId>moco-core</artifactId>
  <version>0.8.1</version>
</dependency>
```

A gradle example is as follow:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  testCompile(
    "com.github.dreamhead:moco-core:0.8.1",
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
    HttpServer server = httpserver(12306);
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

## Standalone

Moco can be used as standalone to run with configuration and you can download standalone directly:
[Standalone Moco Runner](http://repo1.maven.org/maven2/com/github/dreamhead/moco-runner/0.8.1/moco-runner-0.8.1-standalone.jar)

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

It's time to run Moco standalone server:

```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -c foo.json
```

Now, open your browser and input "http://localhost:12306". You will see "foo". That's it.

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
moco start -p 12306 -c foo.json
```

It will download the latest moco automatically if you don't have locally.
