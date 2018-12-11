# Extending Moco

You can extend Moco, if you need more features than Moco already provides.

The basic idea is very simple: RequestMatcher and ResponseHandler. If your request matches any matcher, the corresponding response handler will be invoked to return response.