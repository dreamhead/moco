package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.mount.MountHandler;
import com.github.dreamhead.moco.mount.MountMatcher;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

public abstract class HttpConfiguration extends BaseActualServer<HttpResponseSetting> implements HttpsServer {
    protected HttpConfiguration(final Optional<Integer> port, final MocoMonitor monitor, final MocoConfig[] configs) {
        super(port, monitor, configs);
    }

    @Override
    public HttpResponseSetting get(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.GET, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public HttpResponseSetting post(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.POST, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public HttpResponseSetting put(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.PUT, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public HttpResponseSetting delete(final RequestMatcher matcher) {
        return requestByHttpMethod(HttpMethod.DELETE, checkNotNull(matcher, "Matcher should not be null"));
    }

    @Override
    public HttpResponseSetting mount(final String dir, final MountTo target, final MountPredicate... predicates) {
        File mountedDir = new File(checkNotNullOrEmpty(dir, "Directory should not be null"));
        checkNotNull(target, "Target should not be null");
        return this.request(new MountMatcher(mountedDir, target, copyOf(predicates))).response(new MountHandler(mountedDir, target));
    }

    private HttpResponseSetting requestByHttpMethod(final HttpMethod method, final RequestMatcher matcher) {
        return request(and(by(method(method.name())), matcher));
    }

    @Override
    public HttpResponseSetting proxy(final ProxyConfig config) {
        return proxy(checkNotNull(config, "Proxy config should not be null"), Failover.DEFAULT_FAILOVER);
    }

    @Override
    public HttpResponseSetting proxy(final ProxyConfig proxyConfig, final Failover failover) {
        ProxyConfig config = checkNotNull(proxyConfig, "Proxy config should not be null");
        this.request(InternalApis.context(config.localBase())).response(Moco.proxy(config, checkNotNull(failover, "Failover should not be null")));
        return this;
    }

    @Override
    public HttpResponseSetting redirectTo(final String url) {
        return this.response(status(HttpResponseStatus.FOUND.code()), header(HttpHeaders.LOCATION, checkNotNullOrEmpty(url, "URL should not be null")));
    }

    @Override
    protected HttpResponseSetting onRequestAttached(final RequestMatcher matcher) {
        HttpSetting baseSetting = new HttpSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }
}
