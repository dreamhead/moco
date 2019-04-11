package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.HttpsServer;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.mount.MountHandler;
import com.github.dreamhead.moco.mount.MountMatcher;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.github.dreamhead.moco.util.RedirectDelegate;

import java.io.File;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.method;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

public abstract class HttpConfiguration<T extends BaseActualServer>
        extends BaseActualServer<HttpResponseSetting, T> implements HttpsServer {
    private final RedirectDelegate delegate = new RedirectDelegate();

    protected HttpConfiguration(final int port, final MocoMonitor monitor, final MocoConfig[] configs) {
        super(port, monitor, configs);
    }

    @Override
    public final HttpResponseSetting get(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.GET, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public final HttpResponseSetting post(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.POST, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public final HttpResponseSetting put(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.PUT, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public final HttpResponseSetting delete(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.DELETE, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public final HttpResponseSetting mount(final String dir, final MountTo target, final MountPredicate... predicates) {
        File mountedDir = new File(checkNotNullOrEmpty(dir, "Directory should not be null"));
        checkNotNull(target, "Target should not be null");
        return this.request(new MountMatcher(mountedDir, target, copyOf(predicates)))
                .response(new MountHandler(mountedDir, target));
    }

    private HttpResponseSetting requestByHttpMethod(final HttpMethod method, final RequestMatcher matcher) {
        return request(and(by(method(method)), matcher));
    }

    @Override
    public final HttpResponseSetting proxy(final ProxyConfig config) {
        return proxy(checkNotNull(config, "Proxy config should not be null"), Failover.DEFAULT_FAILOVER);
    }

    @Override
    public final HttpResponseSetting proxy(final ProxyConfig proxyConfig, final Failover failover) {
        ProxyConfig config = checkNotNull(proxyConfig, "Proxy config should not be null");
        this.request(InternalApis.context(config.localBase()))
                .response(Moco.proxy(config, checkNotNull(failover, "Failover should not be null")));
        return this;
    }

    @Override
    public final HttpResponseSetting redirectTo(final String url) {
        return delegate.redirectTo(this, url);
    }

    @Override
    public final HttpResponseSetting redirectTo(final Resource url) {
        return delegate.redirectTo(this, url);
    }

    @Override
    protected final HttpResponseSetting onRequestAttached(final RequestMatcher matcher) {
        HttpSetting baseSetting = new HttpSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }
}
