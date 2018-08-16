# Socket APIs
Moco mainly focuses on server configuration. There are only two kinds of API right now: **Request** and **Response**.

That means if we get the expected request and then return our response. Now, you can see a Moco reference in details.

**WARNING** the json configuration below is just a snippet for one pair of request and response, instead of the whole configuration file.

Table of Contents
=================

* [Composite Java API Design](#composite-java-api-design)
* [Description as comment](#description-as-comment)
* [Request](#request)
  * [Content](#content)
  * [XML](#xml)
  * [XPath](#xpath)
  * [JSON Request](#json-request)
    * [JSON Text](#json-text)
    * [JSON Text Shortcut](#json-text-shortcut)
    * [JSON File](#json-file)
  * [JSONPath](#jsonpath)
  * [Operator](#operator)
    * [Match](#match)
    * [Starts With](#starts-with)
    * [Ends With](#ends-with)
    * [Contain](#contain)
    * [Exist](#exist)
* [Response](#response)
  * [Content](#content-1)
  * [Latency](#latency)
  * [Sequence](#sequence)
  * [JSON Response](#json-response)
* [Template(Beta)](#templatebeta)
  * [Content](#content-2)
  * [Custom Variable](#custom-variable)
  * [File Name Template](#file-name-template)
  
## Composite Java API Design
Moco Java API is designed in functional fashion which means you can composite any request or response easily.

```java
server.request(and(by(uri("/target")), by(version(VERSION_1_0)))).response(with(text("foo")), header("Content-Type", "text/html"));
```

## Description as comment
**@Since 0.7**

In all JSON APIs, you can use **description** to describe what is this session for. It's just used as comment, which will be ignored in runtime.

```json
[
    {
        "description": "any response",
        "response": {
            "text": "foo"
        }
    }
]
```

## Request

### Content
**@Since 0.7**

If you want to response according to request content, Moco server can be configured as following:

* Java API

```java
server.request(by("foo")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

If request content is too large, you can put it in a file:

* Java API

```java
server.request(by(file("foo.request"))).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "file" : "foo.request"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

### XML
**@Since 0.7**

XML is a popular format for Web Services. When a request is in XML, only the XML structure is important in most cases and whitespace can be ignored. The `xml` operator can be used for this case.

* Java API

```java
server.request(xml(text("<request><parameters><id>1</id></parameters></request>"))).response("foo");
```

* JSON

```json
{
  "request":
    {
      "text":
        {
          "xml": "<request><parameters><id>1</id></parameters></request>"
        }
    },
  "response":
    {
      "text": "foo"
    }
}
```

**NOTE**: Please escape the quote in text.

The large request can be put into a file:

```json
{
   "request":
     {
        "file":
          {
            "xml": "your_file.xml"
          }
    },
  "response":
    {
      "text": "foo"
    }
}
```

### XPath
**@Since 0.7**

For the XML/HTML request, Moco allows us to match request with XPath.

* Java API

```java
server.request(eq(xpath("/request/parameters/id/text()"), "1")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "method" : "post",
      "xpaths" :
        {
          "/request/parameters/id/text()" : "1"
        }
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

### JSON Request

#### JSON Text
**@Since 0.7**

Json is rising with RESTful style architecture. Just like XML, in the most case, only JSON structure is important, so `json` operator can be used.

* Java API

```java
server.request(json(text("{\"foo\":\"bar\"}"))).response("foo");
```

* JSON

```json
{
  "request":
    {
      "text":
        {
          "json": "{\"foo\":\"bar\"}"
        }
    },
  "response":
    {
      "text": "foo"
    }
}
```

**NOTE**: Please escape the quote in text.

#### JSON Text Shortcut
**@Since 0.9.2**

As you have seen, it is so boring to write json with escape character, especially in json configuration. So you can try the json shortcut. The upper case could be rewritten as following:

* JSON

```json
{
    "request": {
        "json": {
            "foo": "bar"
        }
    },
    "response": {
        "text": "foo"
    }
}
```

#### JSON File
**@Since 0.7**

The large request can be put into a file:

* Java API

```java
server.request(json(file("your_file.json"))).response("foo");
```

* JSON

```json
{
  "request":
    {
      "file":
        {
          "json": "your_file.json"
        }
    },
  "response":
    {
      "text": "foo"
    }
}
```

### JSONPath
**@Since 0.7**

For the JSON/HTML request, Moco allows us to match request with JSONPath.

* Java API

```java
server.request(eq(jsonPath("$.book[*].price"), "1")).response("response_for_json_path_request");
```

* JSON

```json
{
  "request":
    {
      "json_paths":
        {
          "$.book[*].price": "1"
	    }
    },
  "response":
    {
      "text": "response_for_json_path_request"
    }
}
```

### Operator

Moco also supports some operators which helps you write your expectation easily.

#### Match
**@Since 0.7**

You may want to match your request with regular expression, **match** could be your helper:

* Java API

```java
server.request(match(text("/\\w*/foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "text":
        {
          "match": "/\\w*/foo"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

Moco is implemented by Java regular expression, you can refer [here](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) for more details.

#### Starts With
**@Since 0.9.2**

**starsWith** operator can help you decide if the request information starts with a piece of text.

* Java API

```java
server.request(startsWith(text("/foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "text":
        {
          "startsWith": "/foo"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

#### Ends With
**@Since 0.9.2**

**endsWith** operator can help you decide if the request information ends with a piece of text.

* Java API

```java
server.request(endsWith(text("foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "text":
        {
          "endsWith": "foo"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

#### Contain
**@Since 0.9.2**

**contain** operator helps you know whether the request information contains a piece of text.

* Java API

```java
server.request(contain(text("foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "text":
        {
          "contain": "foo"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

#### Exist
**@Since 0.9.2**

**exist** operator is used to decide whether the request information exists.

* Java API

```java
server.request(exist(header("foo"))).response("bar");
```

* JSON

```json
{
  "request":
    {
      "headers": {
        "foo": {
          "exist" : "true"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

For JSON API, you can decide whether the information does not exist.

* JSON

```json
{
  "request":
    {
      "headers": {
        "foo": {
          "exist" : "not"
        }
    },
  "response":
    {
      "text": "bar"
    }
}
```

## Response

### Content
**@Since 0.7**

As you have seen in previous example, response with content is pretty easy.

* Java API

```java
server.request(by("foo")).response("bar");
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "text" : "bar"
    }
}
```

The same as request, you can response with a file if content is too large to put in a string.

* Java API

```java
server.request(by("foo")).response(file("bar.response"));
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "file" : "bar.response"
    }
}
```

### Latency
**@Since 0.7**

Sometimes, we need a latency to simulate slow server side operation.

* Java API

```java
server.request(by("foo")).response(latency(5000));
```

* JSON

```json
{
  "request" :
    {
      "text" : "foo"
    },
  "response" :
    {
      "latency" : 5000
    }
}
```

**@Since 0.10.1**

It's easy to setup latency with time unit.

```java
server.response(latency(1, TimeUnit.SECONDS));
```

### Sequence
**@Since 0.7**

Sometimes, we want to simulate a real-world operation which change server side resource. For example:
* First time you request a resource and "foo" is returned
* We update this resource
* Again request the same URL, updated content, e.g. "bar" is expected.

We can do that by

```java
server.request(by(text("/foo"))).response(seq("foo", "bar", "blah"));
```

### JSON Response

JSON response is the API without Java API, so if response is json, we don't have to write json with escape character. Hint, json api will setup Content-Type header as well.

```json
{
    "request": {
        "text": "json"
    },
    "response": {
        "json": {
            "foo" : "bar"
        }
    }
}
```

## Template(Beta)
**Note**: Template is an experimental feature which could be changed a lot in the future. Feel free to tell how it helps or you need more features in template.

Sometimes, we need to customize our response based on something, e.g. response should have same header with request.

The goal can be reached by template:

### Content
**@Since 0.8**

All request content can be used in template with "req.content"

* Java

```java
server.request(by(text("template"))).response(template("${req.content}"));
```

* JSON

```json
{
    "request": {
        "text": "template"
    },
    "response": {
        "text": {
            "template": "${req.content}"
        }
    }
}
```

### Custom Variable
**@Since 0.9.1**

You can provide your own variables in your template.

* Java

```java
server.request(by(text("template"))).response(template("${'foo'}", "foo", "bar"));
```

* JSON

```json
{
    "request": {
        "text": "template"
    },
    "response": {
        "text": {
            "template": {
                "with" : "${'foo'}",
                "vars" : {
                    "foo" : "bar"
                }
            }
        }
    }
}
```

**@Since 0.10.0**

You can also use extractor to extract information from request.

* Java

```java
server.request(by(text("template"))).response(template("${'foo'}", "foo", jsonPath("$.book[*].price")));
```

* JSON

```json
{
    "request": {
        "text": "template"
    },
    "response": {
        "text": {
            "template": {
                "with" : "${'foo'}",
                "vars" : {
                    "foo" : {
                      "json_paths": "$.book[*].price"
                    }
                }
            }
        }
    }
}
```

Other extractors, e.g. xpath also work here.

### File Name Template

Template can also be used in file name, thus response can be different based on different request.

* Java

```java
server.response(file(template("${req.headers['foo'].txt")));
```

* JSON

```json
[
  {
    "response": {
      "file": {
        "name": {
          "template": "${req.content}.txt"
        }
      }
    }
  }
]
```
