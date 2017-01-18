package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.RestIdMatcher;

import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;

public final class RestIdMatchers {
    public static RestIdMatcher anyId() {
        return new BaseRestIdMatcher("[^/]*");
    }

    public static RestIdMatcher eq(final String id) {
        return new BaseRestIdMatcher(checkNotNullOrEmpty(id, "ID should not be null or empty"));
    }

    public static RestIdMatcher match(final String uri) {
        return new BaseRestIdMatcher(checkNotNullOrEmpty(uri, "Match target should not be null or empty"));
    }

    private static class BaseRestIdMatcher implements RestIdMatcher {
        private String uriPart;

        BaseRestIdMatcher(final String uriPart) {
            this.uriPart = uriPart;
        }

        @Override
        public String resourceUri() {
            return this.uriPart;
        }

        @Override
        public RequestMatcher matcher(final RestIdMatcher resourceName) {
            return Moco.match(uri(subResourceUri(resourceName)));
        }

        private String subResourceUri(final RestIdMatcher resourceName) {
            return join(resourceRoot(resourceName.resourceUri()), this.uriPart);
        }
    }

    private RestIdMatchers() {
    }
}
