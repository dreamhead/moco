# Standalone Command Line

Moco can be used as standalone to run with configuration and you can download standalone directly:
[Standalone Moco Runner](https://repo1.maven.org/maven2/com/github/dreamhead/moco-runner/0.10.2/moco-runner-0.10.2-standalone.jar)

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

**Table of Contents**

* [HTTP Server](#http-server)
* [HTTPS Server](#https-server)
* [Socket Server](#socket-server)
* [Port](#port)
* [Version](#version)
* [Global Settings](#global-settings)
  * [Environment](#environment)
* [Shutdown](#shutdown)

## HTTP Server

You can start a HTTP server by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -c foo.json
```

## HTTPS Server

A HTTPS server can be started by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar https -p 12306 -c foo.json --https /path/to/cert.jks --cert mocohttps --keystore mocohttps
```

## Socket Server

A socket server can be started by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar socket -p 12306 -c foo.json
```

## Port

If you don't need any specified port, you run run Moco without port.

```shell
java -jar moco-runner-<version>-standalone.jar http -c foo.json
```

An available port will picked up by Moco and you can see the port in console.

```shell
20 Oct 2015 22:10:18 [main] INFO  Server is started at 58593
20 Oct 2015 22:10:18 [main] INFO  Shutdown port is 58594
```

## Version

You can query Moco version by the following command: 

```shell
java -jar moco-runner-<version>-standalone.jar version
```

## Global Settings

You can run Moco instance with [global settings](global-settings.md).

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -g settings.json
```

### Environment

[Environment](global-settings.md#environment) is a good feature, which allows you start your server with different environment from CLI.

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -g env.json -e remote
```

## Shutdown

Moco instance can ben shutdown by shutdown command and its shutdown port. A shutdown port can be specified when start Moco instance.

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -c foo.json -s 9527
```

Or leave Moco to choose by default, the shutdown port will be shown on console.

Then you can use the shutdown port to shutdown the running Moco instance.

```shell
java -jar moco-runner-<version>-standalone.jar shutdown -s 9527
```
