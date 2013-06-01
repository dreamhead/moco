# Settings
We can write all our configurations in one single configuration files. But if we want stub many services in a single Moco instance, the configurations file would be huge.

In this case, we can use settings file to separate our configurations for different into different configugration files.

It's example time. We have two services to stub:

```json
[
    {
        "request" : {
            "uri" : "/foo"
        },
        "response" : {
            "text" : "foo"
        }
    }
]
```

and

```json
[
    {
        "request" : {
            "uri" : "/bar"
        },
        "response" : {
            "text" : "bar"
        }
    }
]
```

Now, we can write a setting file to combine these two configurations:

```json
[
    {
        "include" : "src/test/resources/multiple/foo.json"
    },
    {
        "include" : "src/test/resources/multiple/bar.json"
    }
]
```

It's time start server with this setting:

```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -g settings.json
```

Feel free to open your browser to try.

## Configuration

In fact, there are some configurations for settings to simplify your configuration.

## Context

We can put all responses for one service in a specified context:

```json
[
    {
        "context": "/foo",
        "include": "src/test/resources/multiple/foo.json"
    },
    {
        "context": "/bar",
        "include": "src/test/resources/multiple/bar.json"
    }
]
```

Now all configurations in foo.json must be accessed by /foo context.