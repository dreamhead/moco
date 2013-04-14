[![Build Status](https://travis-ci.org/dreamhead/moco.png?branch=master)](https://travis-ci.org/dreamhead/moco)

Moco is an easy setup stub framework, mainly focusing on testing and integration, inspired by Mock framework, e.g. [Mockito](http://code.google.com/p/mockito/), and [Playframework](http://www.playframework.com/)

# Why
Integration, especially based on HTTP protocol, e.g. web service, REST etc, is wildly used in most of our development.

In the old days, we just deployed another WAR to an application server, e.g. Jetty or Tomcat etc. As we all know, it's so boring to develop a WAR and deploy it to any application server, even if we use an embeded server. And the WAR needs to be reassembled even if we just want to change a little bit.

# Quick Start
* Download [Standalone Moco Runner](https://oss.sonatype.org/content/groups/public/com/github/dreamhead/moco-runner/0.6.3-SNAPSHOT/moco-runner-0.6.3-20130314.090338-1-standalone.jar)
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

* Run Moco server
```shell
java -jar moco-runner-<version>-standalone.jar start -p 12306 -c foo.json
```

* Now, open your favorite browser to visit http://localhost:12306 and you will see "Hello, Moco".

# Documents
* More [Usage](https://github.com/dreamhead/moco/blob/master/moco-doc/usage.md)
* Detailed [Configurations](https://github.com/dreamhead/moco/blob/master/moco-doc/configurations.md)

# Build
Make sure you have JDK and Gradle installed.

* Clone Moco

```shell
git clone git@github.com:dreamhead/moco.git
```
* Build Moco

```shell
gradle build
```
* Build uberjar

```shell
gradle uberjar
```
* Check code before commit

```shell
gradle check
```

# Contributing
Check out what you can help [here](https://github.com/dreamhead/moco/blob/master/moco-doc/plan.md) if you do not have any existing idea.

# Copyright and license
Copyright 2013 ZHENG Ye

Licensed under MIT License (the "License"); You may obtain a copy of the License in the LICENSE file, or at:

https://raw.github.com/dreamhead/moco/master/MIT-LICENSE.txt