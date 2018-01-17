package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;

public interface HttpServer extends HttpResponseSetting, Server<HttpResponseSetting> {
    HttpResponseSetting get(RequestMatcher matcher);

    HttpResponseSetting post(RequestMatcher matcher);

    HttpResponseSetting put(RequestMatcher matcher);

    HttpResponseSetting delete(RequestMatcher matcher);

    HttpResponseSetting mount(String dir, MountTo target, MountPredicate... predicates);

    HttpResponseSetting proxy(ProxyConfig config);

    HttpResponseSetting proxy(ProxyConfig proxyConfig, Failover failover);
}
