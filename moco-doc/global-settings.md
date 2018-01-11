# Global Settings
We could put all configurations in one single configuration files. But if we want stub many services in a single Moco instance, the configurations file would be huge.

In this case, we can use settings file to separate our configurations for different into different configuration files.

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
java -jar moco-runner-<version>-standalone.jar http -p 12306 -g settings.json
```

Feel free to open your browser to try.

Table of Contents
=================

* [Configuration](#configuration)
  * [Context](#context)
  * [File Root](#file-root)
  * [Environment](#environment)
  * [Request](#request)
  * [Response](#response)
  * [Glob Inclusion](#glob-inclusion)

## Configuration

In fact, there are some configurations for settings to simplify your configuration.

### Context
**@Since 0.8**

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

Now all configurations in foo.json must be accessed by /foo context. In this case, when you access http://localhost:12306/foo/foo, you will get "foo".

On the other hand, bar.json will in /bar context which means http://localhost:12306/bar/bar will return "bar".

### File Root
**@Since 0.8**

If you have many file APIs in your configuration, file root setting will help you to shorten configurations.
As the name suggests, file root setting will play as the file root for configuration. So all your file APIs could be used as the relative path.

```json
[
    {
        "file_root": "fileroot",
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

### Environment
**@Since 0.8**

For some different cases, you have different configuration. For example, your code may access proxy for remote server and mock server for local testing.

Now, environment will help you to configure all related configurations in one settings.

```json
[
    {
        "env" : "remote",
        "include": "foo.json"
    },
    {
        "env" : "local",
        "include": "bar.json"
    }
]
```
(env.json)

You can start your server with different environment from CLI.

```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -g env.json -e remote
```

Now, when you access your server, all configurations with "remote" environment rocks!

In this case, http://localhost:12306/foo will give you "foo", but http://localhost:12306/bar will return nothing.

### Request
**@Since 0.10.1**

You may need a global request matcher in some cases, for example, token for some REST APIs, which actually is request parameter. Global request will help you.

```json
[
  {
    "request" : {
      "headers" : {
        "foo" : "bar"
      }
    },
    "include": "blah.json"
  }
]
```

In this case, you won't receive response until your request has "foo", "bar" header.

### Response
**@Since 0.9.1**

In some cases, you may want to setup a global response for all response, for example, HTTP version, or HTTP header, so you don't have to setup it for every single response.

```json
[
    {
        "response" : {
            "headers" : {
                "foo" : "bar"
            }
        },
        "include": "blah.json"
    }
]
```

When you issue any request to server, it will return response with "foo", "bar" header.

### Glob Inclusion

**@Since 0.12.0**

You may need include many files, glob can help you to do this.

```json
[
    {
        "include" : "*.json"
    }
]
```
