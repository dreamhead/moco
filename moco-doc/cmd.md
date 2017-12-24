# Standalone Command Line

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

Table of Contents
=================

* [Server Type](#server-type)
  * [HTTP Server](#http-server)
  * [HTTPS Server](#https-server)
  * [Socket Server](#socket-server)
* [Configuration Files](#configuration-files)
  * [One Configuration File](#one-configuration-file)
  * [Many Configuration Files](#many-configuration-files)
* [Port](#port)
  * [Specific Port](#specific-port)
  * [Without Port](#without-port)
* [Version](#version)
* [Global Settings](#global-settings)
  * [Environment](#environment)
* [Shutdown](#shutdown)

## Server Type

A server type is a must for Moco, you need specify your server type as first argument. Different server type will support different arguments.

### HTTP Server

You can start a HTTP server by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -c foo.json
```

### HTTPS Server

A HTTPS server can be started by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar https -p 12306 -c foo.json --https /path/to/cert.jks --cert mocohttps --keystore mocohttps
```

### Socket Server

A socket server can be started by the following command:

```shell
java -jar moco-runner-<version>-standalone.jar socket -p 12306 -c foo.json
```

## Configuration Files

### One Configuration File

You can specify your configuration with `-c`.

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -c foo.json
```

### Many Configuration Files
**@Since 0.12.0**

If you have many configuration files, you can use glob matcher as `-c` argument.
But you need to make sure the configuration that don't conflict.

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -c "*.json"
```

Note: the quotation mark is required otherwise *.json will be parsed by your shell.

## Port

### Specific Port

You can specify server port with `-p`

```shell
java -jar moco-runner-<version>-standalone.jar http -p 12306 -c foo.json
```

### Without Port

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
