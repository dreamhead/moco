# Release 0.12.0

## APIs
* `cycle` API returns response as a cycle.
* `now` as template function is supported in template
* `random` as template function is supported in template
* `req.json` is supported in template
* `failover` and `playback` status API is supported.

## Implementation Details
* Support multiple HTTP headers with same name
* Support proxy PATCH method
* Dump HTTP message even if Content Length header is missing

# Release 0.12.0 (24-Dec-2017)

* Upgrade minimal Java version to Java 7

## APIs
* `json` API returns resource, so it can be used for both request and response.
* `and` API for Response Handler

## JSON APIs
* `seq` is supported finally
* `json` in Post event

## Global Settings
* Glob syntax is supported for include files in global setting

## CLI
* Glob syntax is supported for CLI '-c' argument

# Release 0.11.1 (12-Jun-2017)

## APIs
* Template for Event action
* Remove publish API with Optional, e.g. file and pathResource
* Proxy with content resource as url
* Add cookie attribute API

## JUnit
* HTTPS JUnit runner for POJO, JSON and from path resource
* Http JSON runner with path resource
* Socket JSON runner with path resource

## Implementation Details
* Fix: get Moco standalone version from manifest
* Fix: merge server with port
* Fix: set charset from http request
* Fix: parse json with charset
* Fix: fail to load configuration with multiple modification
* Fix: allowed empty query
* Fix: make request hit and moco monitor thread safe
* Fix: parse form with charset

# Release 0.11.0 (20-May-2016)
 
## Usage
* JUnit Integration

## APIs
### REST API
* get
* post
* put
* delete
* head
* patch
* sub-resource
* any id

## Implementation Details
* Fix: response handler case issue
* Write JSON content to log
* Fix: empty content for jsonpath
* Fix: empty content for xpath

# Release 0.10.2 (1-Sep-2015)

## APIs
* **toJson** will convert POJO to JSON text in response handler
* **json** with pojo will convert POJO to JSON text in request matcher
* **socketServer** without port
* **socketServer** with monitor, e.g. logging and verification
* Template as **redirect** target 

## Implementation Details
* Enhance log dumper with MediaType implementation
* Enhance socket server implementation with byte buf, rather than string
* Fix charset issue in toJson
* Enhance file content type with MediaType implementation
* Same name query has been supported

# Release 0.10.1 (1-May-2015)

## APIs
* Use **httpServer** as API, and **httpserver** has been deprecated, will be removed at next release.
* File and path resource with charset
* Latency with time unit, and latency without time unit has been deprecated, will be removed at next release
* Log file with charset
* Template as filename for file and path resource

## JSON APIs
* Mount with response setting

## Global Settings
* Request

## Implementation Details
* File and path resource read as byte array to fix file encoding issue
* JSON path and XPath in template will return many objects.
* Changed template extractor name to singular in JSON API, e.g. json_paths to **json_path**, xpaths to **xpath**.

# Release 0.10.0 (1-Dec-2014)

## Usage
* Socket support
* Version query
* Start arguments have been changed from start to HTTP(HTTPS, Socket) etc.

## APIs
* Attachment

## Runner API
* server by JSON configuration

## Template
* Template extractor support

## Shell
* Added version argument

## Potential Breaks
* JSON Path, because new version JSON Path implementation has been introduced.

# Release 0.9.2 (1-Jul-2014)

## Usage
* (Beta) HTTPS support

## APIs
* Http Protocol Version in Java API.
* More operators:
** startsWith
** endsWith
** contain
** exist
* between for verifier
* Added multiple monitors for server creation.

## JSON APIs
* json text shortcut

### Template
* change template variables from Object to String.
  (API users have to decide how to display their vars).
* JSON request/response shortcut in JSON configuration.

# Release 0.9.1 (1-Feb-2014)
## Usage
* Runner API
* (Beta) gmoco, moco groovy shell

## APIs
* log to record request
* once for verifier
* batch proxy
* proxy playback

### Template
* cookie
* form
* template variables

### Global Settings
* Response

## Implementation Details
* Fixed "uri" in failover to remove query parameter.
* Used Proguard to minimize standalone jar

## Fixes
* Keep connect alive if request wants to keep alive
* NPE for validating form/content in request hit
* NPE for XPath matcher
* Wait server to shutdown completely

# Release 0.9 (1-Nov-2013)

## APIs
* Request Hit as verifier
* Event API
** Event: Complete
** Event Action: Get/Post URL
* Turned off log for API by default
* seq API with response handler
* Added shortcut for PUT and DELETE method

## Implementation Details
* Server can select available port(start/shutdown) without providing no port.
* Changed "statusCode" in failover to "status". Please changed your failover file if your tests failed.

# Release 0.8.1 (1-August-2013)
* Upgrade to Netty 4
* Fixed proxy bug

# Release 0.8 (21-July-2013)

## Usage
* global setting file

## APIs
### Request
* JSONPath

### Response
#### Integration
* proxy
* with
* (Beta) template
* removed url (using proxy instead)
* removed content (using with instead)

#### Misc Response
* removed cache

#### Template
* version
* method
* content
* header
* query

## Global Settings
* context
* file root
* environment

# Release 0.7 (1-May-2013)

the first public release of Moco

## Usage
* API
* stanalone server
* shell
* maven plugin (external)
* gradle plugin (external)

## APIs

### Request

#### HTTP
* content(text, file)
* URI
* query Parameter
* HTTP method
* version
* header
* redirect

#### Operator
* eq
* match

#### Web
* cookie
* form

#### Integration
* XML
* XPath
* JSON

### Response

#### HTTP
* content(text, file)
* version
* status code
* header

#### Integration
* url

#### Misc Response
* latency
* sequence(API only)
* cache

### Misc
* mount