package com.github.dreamhead.moco;

import com.google.common.base.Optional;

public interface RestIdMatcher {
    RequestMatcher matcher(final RestIdMatcher resourceName);
    String resourceUri(final Optional<RestIdMatcher> resourceName);
}
