# Standalone Command Line

Moco can be used as standalone to run with configuration and you can download standalone directly:
[Standalone Moco Runner](http://repo1.maven.org/maven2/com/github/dreamhead/moco-runner/0.9.1/moco-runner-0.9.1-standalone.jar)

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

## HTTP Server

You can start a HTTP server by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -c foo.json
```

## HTTPS Server

A HTTPS server can be started by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -c foo.json --https /path/to/cert.jks --cert mocohttps --keystore mocohttps
```