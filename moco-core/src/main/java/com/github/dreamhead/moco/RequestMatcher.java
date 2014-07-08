package com.github.dreamhead.moco;

public interface RequestMatcher extends ConfigApplier<RequestMatcher> {
    boolean match(final Request request);
}
