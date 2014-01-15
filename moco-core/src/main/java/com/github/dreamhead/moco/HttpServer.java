package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.mount.MountHandler;
import com.github.dreamhead.moco.mount.MountMatcher;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

public abstract class HttpServer extends ResponseSetting {
    public abstract int port();

    protected abstract Setting onRequestAttached(RequestMatcher matcher);

    public Setting request(final RequestMatcher matcher) {
        return this.onRequestAttached(checkNotNull(matcher, "Matcher should not be null"));
    }

    public Setting request(final RequestMatcher... matchers) {
        return request(or(matchers));
    }

    public Setting get(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.GET, checkNotNull(matcher, "Matcher should not be null"));
    }

    public Setting post(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.POST, checkNotNull(matcher, "Matcher should not be null"));
    }

    public Setting put(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.PUT, checkNotNull(matcher, "Matcher should not be null"));
    }

    public Setting delete(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.DELETE, checkNotNull(matcher, "Matcher should not be null"));
    }

    public ResponseSetting mount(final String dir, final MountTo target, final MountPredicate... predicates) {
        File mountedDir = new File(checkNotNull(dir, "Directory should not be null"));
        checkNotNull(target, "Target should not be null");
        this.request(new MountMatcher(mountedDir, target, copyOf(predicates))).response(new MountHandler(mountedDir, target));
        return this;
    }

    private Setting requestByHttpMethod(final HttpMethod method, final RequestMatcher matcher) {
        return request(and(by(method(method.name())), matcher));
    }

    public void proxy(ProxyConfig config) {
        this.request(context(config.localBase())).response(Moco.proxy(config));
    }

    public void proxy(ProxyConfig config, Failover failover) {
        this.request(context(config.localBase())).response(Moco.proxy(config, failover));
    }
}
