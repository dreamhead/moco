package com.github.dreamhead.moco;

import com.github.dreamhead.moco.mount.MountHandler;
import com.github.dreamhead.moco.mount.MountMatcher;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.io.File;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.ImmutableList.copyOf;
import static java.lang.String.format;

public abstract class HttpServer extends ResponseSetting {
    protected abstract Setting onRequestAttached(RequestMatcher matcher);

    public Setting request(RequestMatcher matcher) {
        return this.onRequestAttached(matcher);
    }

    public Setting request(RequestMatcher... matchers) {
        return request(or(matchers));
    }

    public Setting get(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.GET.getName())), matcher));
    }

    public Setting post(RequestMatcher matcher) {
        return request(and(by(method(HttpMethod.POST.getName())), matcher));
    }

    public void mount(final String dir, final MountTo target, final MountPredicate... predicates) {
        File mountedDir = new File(dir);
        this.request(new MountMatcher(mountedDir, target, copyOf(predicates))).response(new MountHandler(mountedDir, target));
    }
}
