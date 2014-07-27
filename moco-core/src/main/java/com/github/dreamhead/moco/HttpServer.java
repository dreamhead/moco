package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;

public interface HttpServer extends HttpResponseSetting {
    int port();

    HttpSetting request(final RequestMatcher matcher);

    HttpSetting request(final RequestMatcher... matchers);

    HttpSetting get(final RequestMatcher matcher);

    HttpSetting post(final RequestMatcher matcher);

    HttpSetting put(final RequestMatcher matcher);

    HttpSetting delete(final RequestMatcher matcher);

    HttpResponseSetting mount(final String dir, final MountTo target, final MountPredicate... predicates);

    HttpResponseSetting proxy(final ProxyConfig config);

    HttpResponseSetting proxy(final ProxyConfig proxyConfig, final Failover failover);
}
