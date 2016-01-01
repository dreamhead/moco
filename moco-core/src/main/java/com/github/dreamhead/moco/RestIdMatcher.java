package com.github.dreamhead.moco;

public interface RestIdMatcher {
    RequestMatcher matcher(final String resourceName);
}
