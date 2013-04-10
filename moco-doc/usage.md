# Dependencies
Moco has been published on Maven repository, so you can refer to it directly in your code.

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
[Standalone Moco Runner](https://oss.sonatype.org/content/groups/public/com/github/dreamhead/moco-runner/0.6.4-SNAPSHOT/moco-runner-0.6.4-20130326.082119-1-standalone.jar)

# Usage
You have several ways to use Moco. One is API, which you can use in your unit test. The other is that run Moco as standalone. Currently, you put all your configuration in JSON file.

## API
Here is an typical Moco test case in JUnit.

```java
@Test
public void should_response_as_expected() {
  HttpServer server = httpserver(12306);
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
java -jar moco-runner-<version>-standalone.jar start -p 12306 -c foo.json
```

Now, open your browser and input "http://localhost:12306". You will see "foo". That's it.

# Maven Plugin

Moco also can be used as Maven plugin.

https://github.com/GarrettHeel/moco-maven-plugin

# Shell

If you are using Mac or Linux, you may try the following approach:

* Make sure you have JDK 6 or later.
* [Download the script](https://raw.github.com/dreamhead/moco/master/moco-shell/moco).
* Place it on your $PATH. (~/bin is a good choice if it is on your path.)
* Set it to be executable. (chmod 755 ~/bin/moco)

Now, you can try
```shell
moco start -p 12306 -c foo.json
```

It will download the latest moco automatically if you don't have locally.
