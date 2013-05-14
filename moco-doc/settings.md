# Settings
We can write all our configurations in one single configuration files. But if we want stub many services in one Moco instance, the configurations file would be huge.

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