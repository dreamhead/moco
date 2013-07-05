# Settings
We could put all configurations in one single configuration files. But if we want stub many services in a single Moco instance, the configurations file would be huge.

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
        "include" : "foo.json"
    },
    {
        "include" : "bar.json"
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
        "include": "foo.json"
    },
    {
        "context": "/bar",
        "include": "bar.json"
    }
]
```

Now all configurations in foo.json must be accessed by /foo context.

## File Root

If you have many file APIs in your configuration, file root setting will help you to shorten configurations.
As the name suggests, file root setting will play as the file root for configuration. So all your file APIs could be used as the relative path.

```json
[
    {
        "file_root": "fileroot"
        "include": "fileroot.json"
    }
]
```

Now, both include setting and file APIs use relative path. In this case, fileroot/fileroot.json will be used as included file.

```json
[
    {
        "request" : {
            "uri" : "/fileroot"
        },
        "response" : {
            "file" : "foo.response"
        }
    }
]
```
(fileroot/fileroot.json)

When the request is launched, src/test/resources/foo.response will be returned.

## Environment

For some different cases, you have different configuration. For example, your code may access proxy for remote server and mock server for local testing.

Now, environment will help you to configure all related configurations in one settings.

```json
[
    {
        "env" : "remote"
        "include": "remote.json",
    },
    {
        "env" : "local"
        "include": "local.json",
    }
]
```

You can start your server with different environment from CLI.
```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -g settings.json -e remote
```

Now, when you access your server, all configurations with "remote" environment rocks!
