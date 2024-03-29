package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.github.dreamhead.moco.RestSetting;

import java.util.Optional;

import static com.github.dreamhead.moco.Moco.and;
import static java.util.Optional.of;

public abstract class SimpleRestSetting implements RestSetting {
    private final HttpMethod method;
    private final Optional<RequestMatcher> matcher;
    private final ResponseHandler handler;

    protected abstract RequestMatcher getBaseRequestMatcher(RestIdMatcher resourceName);

    public SimpleRestSetting(final HttpMethod method,
                             final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        this.method = method;
        this.matcher = matcher;
        this.handler = handler;
    }

    public final ResponseHandler getHandler() {
        return handler;
    }

    public final RequestMatcher getRequestMatcher(final RestIdMatcher resourceName) {
        RequestMatcher rootMatcher = getBaseRequestMatcher(resourceName);
        return this.matcher.map(requestMatcher -> and(rootMatcher, requestMatcher)).orElse(rootMatcher);

    }

    public final boolean isFor(final HttpMethod method) {
        return this.method == method;
    }

    @Override
    public final boolean isSimple() {
        return true;
    }

    @Override
    public final Optional<ResponseHandler> getMatched(final RestIdMatcher resourceName, final HttpRequest httpRequest) {
        if (getRequestMatcher(resourceName).match(httpRequest)) {
            return of(handler);
        }

        return Optional.empty();
    }
}
