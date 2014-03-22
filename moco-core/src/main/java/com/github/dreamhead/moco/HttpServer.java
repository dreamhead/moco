package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;

public interface HttpServer extends ResponseSetting {
    int port();

    Setting request(final RequestMatcher matcher);

    Setting request(final RequestMatcher... matchers);

    Setting get(final RequestMatcher matcher);

    Setting post(final RequestMatcher matcher);

    Setting put(final RequestMatcher matcher);

    Setting delete(final RequestMatcher matcher);

    ResponseSetting mount(final String dir, final MountTo target, final MountPredicate... predicates);

    ResponseSetting proxy(final ProxyConfig config);

    ResponseSetting proxy(final ProxyConfig proxyConfig, final Failover failover);
}
