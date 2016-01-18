package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.RestIdMatcher;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.base.Optional.of;

public final class RestIdMatchers {
    public static RestIdMatcher anyId() {
        return new BaseRestIdMatcher("[^/]*") {
            @Override
            public RequestMatcher matcher(final RestIdMatcher resourceName) {
                return Moco.match(uri(resourceUri(of(resourceName))));
            }
        };
    }

    public static RestIdMatcher eq(final String id) {
        return new BaseRestIdMatcher(id) {
            @Override
            public RequestMatcher matcher(final RestIdMatcher resourceName) {
                return Moco.match(uri(resourceUri(of(resourceName))));
            }
        };
    }

    public static RestIdMatcher match(final String uri) {
        return new BaseRestIdMatcher(uri) {
            @Override
            public RequestMatcher matcher(final RestIdMatcher resourceName) {
                return Moco.match(uri(resourceUri(of(resourceName))));
            }
        };
    }

    private abstract static class BaseRestIdMatcher implements RestIdMatcher {
        private String uriPart;

        BaseRestIdMatcher(final String uriPart) {
            this.uriPart = uriPart;
        }

        @Override
        public String resourceUri(final Optional<RestIdMatcher> resourceName) {
            if (!resourceName.isPresent()) {
                return this.uriPart;
            }

            return join(resourceRoot(resourceName.get().resourceUri(Optional.<RestIdMatcher>absent())), this.uriPart);
        }
    }

    private RestIdMatchers() {
    }
}
