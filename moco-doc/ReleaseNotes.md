# Release 0.10.0

## Usage
* Socket support
* Version query 

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