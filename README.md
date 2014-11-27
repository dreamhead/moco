<img src="moco-doc/DukeChoice-960x90-lm.png?raw=true">

<a href="https://github.com/dreamhead/moco">
  <img src="moco-doc/moco.png?raw=true" width="100px">
</a>

# [Moco](https://github.com/dreamhead/moco) [![Build Status](https://travis-ci.org/dreamhead/moco.png?branch=master)](https://travis-ci.org/dreamhead/moco)

Moco is an easy setup stub framework.

## Latest Release
* __0.9.2__

More details in [Release Notes](moco-doc/ReleaseNotes.md)

## User Voice
* [Let me know](https://jinshuju.net/f/Agawf9) if you are using Moco.
* Join Moco [mailing list](https://groups.google.com/forum/#!forum/moco-stub) to discuss.

## Why
Integration, especially based on HTTP protocol, e.g. web service, REST etc, is wildly used in most of our development.

In the old days, we just deployed another WAR to an application server, e.g. Jetty or Tomcat etc. As we all know, it's so boring to develop a WAR and deploy it to any application server, even if we use an embeded server. And the WAR needs to be reassembled even if we just want to change a little bit.

## Quick Start
* Download [Standalone Moco Runner](http://repo1.maven.org/maven2/com/github/dreamhead/moco-runner/0.9.2/moco-runner-0.9.2-standalone.jar)
* Write your own configuration file to describe your Moco server configuration as follow:
```json
[
  {
    "response" :
      {
        "text" : "Hello, Moco"
      }
  }
]
```

* Run Moco http server
```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -c foo.json
```

* Now, open your favorite browser to visit http://localhost:12306 and you will see "Hello, Moco".

## Documents
* More [Usages](moco-doc/usage.md)
* Detailed [HTTP APIs](moco-doc/apis.md) or [Socket APIs](moco-doc/socket-apis.md)
* [Global Settings](moco-doc/global-settings.md) for multiple configuration files.
* [Command Line Usages](moco-doc/cmd.md)

## Build
Make sure you have JDK and Gradle installed.

* Clone Moco

```shell
git clone git@github.com:dreamhead/moco.git
```
* Build Moco

```shell
./gradlew build
```
* Build uberjar

```shell
./gradlew uberjar
```
* Check code before commit

```shell
./gradlew check
```

## Contributing
Check out what you can help [here](moco-doc/plan.md) if you do not have any existing idea.

## Copyright and license
Copyright 2014 ZHENG Ye

Licensed under MIT License (the "License"); You may obtain a copy of the License in the LICENSE file, or at:

https://raw.github.com/dreamhead/moco/master/MIT-LICENSE.txt
