package com.github.dreamhead.moco;

public interface RestIdMatcher {
    RequestMatcher matcher(RestIdMatcher resourceName);
    String resourceUri();
}
