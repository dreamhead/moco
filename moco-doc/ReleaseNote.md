# Release 1.0
## APIs
* log to record request

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

## Settings
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

#### Web
* cookie

#### Integration
* url

#### Misc Response
* latency
* sequence(API only)
* cache

### Misc
* mount