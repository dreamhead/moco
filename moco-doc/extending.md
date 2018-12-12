# Extending Moco

You can extend Moco, if you need more features than Moco already provides.

The basic idea is very simple: RequestMatcher and ResponseHandler. If your request matches any matcher, the corresponding response handler will be invoked to return response.

## Response Handler

If you want to write response based on the request, simply write a `ResponseHandler`.

```java
public interface ResponseHandler extends ConfigApplier<ResponseHandler> {
    void writeToResponse(SessionContext context);
}
```

You just want to rewrite your own HTTP content in most cases, you can extend `AbstractHttpContentResponseHandler`.

```java
public class AbstractContentResponseHandler extends AbstractHttpContentResponseHandler {
    protected MessageContent responseContent(HttpRequest httpRequest) {
        return MessageContent.content("hello Moco"); 
    }
    
    protected abstract MediaType getContentType(HttpRequest request) {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
}
```