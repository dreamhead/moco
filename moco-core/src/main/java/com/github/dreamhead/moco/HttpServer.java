package com.github.dreamhead.moco;

import com.github.dreamhead.moco.mount.MountHandler;
import com.github.dreamhead.moco.mount.MountMatcher;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.ImmutableList.copyOf;

public abstract class HttpServer extends ResponseSetting {
    protected abstract Setting onRequestAttached(RequestMatcher matcher);

    public Setting request(RequestMatcher matcher) {
        return this.onRequestAttached(matcher);
    }

    public Setting request(RequestMatcher... matchers) {
        return request(or(matchers));
    }

    public Setting get(RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.GET, matcher);
    }

    public Setting post(RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.POST, matcher);
    }

    public Setting put(RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.PUT, matcher);
    }

    public Setting delete(RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.DELETE, matcher);
    }

    public Setting put(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.PUT.name())), matcher));
    }

    public Setting trace(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.TRACE.name())), matcher));
    }

    public Setting connect(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.CONNECT.name())), matcher));
    }

    public Setting patch(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.PATCH.name())), matcher));
    }

    public Setting delete(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.DELETE.name())), matcher));
    }

    public Setting head(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.HEAD.name())), matcher));
    }

    public Setting options(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.OPTIONS.name())), matcher));
    }

    public void mount(final String dir, final MountTo target, final MountPredicate... predicates) {
        File mountedDir = new File(dir);
        this.request(new MountMatcher(mountedDir, target, copyOf(predicates))).response(new MountHandler(mountedDir, target));
    }

    private Setting requestByHttpMethod(HttpMethod method, RequestMatcher matcher) {
        return request(and(by(method(method.name())), matcher));
    }
}
