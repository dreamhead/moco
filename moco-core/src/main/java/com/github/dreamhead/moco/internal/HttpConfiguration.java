package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.HttpsServer;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.mount.MountHandler;
import com.github.dreamhead.moco.mount.MountMatcher;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.rest.RestSetting;
import com.github.dreamhead.moco.setting.HttpSetting;
import com.github.dreamhead.moco.util.RedirectDelegate;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.method;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

public abstract class HttpConfiguration extends BaseActualServer<HttpResponseSetting> implements HttpsServer {
    private final RedirectDelegate delegate = new RedirectDelegate();

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
        return this.request(new MountMatcher(mountedDir, target, copyOf(predicates)))
                .response(new MountHandler(mountedDir, target));
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
        this.request(InternalApis.context(config.localBase()))
                .response(Moco.proxy(config, checkNotNull(failover, "Failover should not be null")));
        return this;
    }

    @Override
    public HttpResponseSetting redirectTo(final String url) {
        return delegate.redirectTo(this, url);
    }

    @Override
    public HttpResponseSetting redirectTo(final Resource url) {
        return delegate.redirectTo(this, url);
    }

    @Override
    public void resource(final String name, final RestSetting... settings) {
        checkNotNull(name, "Resource name should not be null");
        checkNotNull(settings, "Rest settings should not be null");

        for (RestSetting setting : settings) {
            this.get(by(uri(join(resourceRoot(name), setting.getId())))).response(setting.getHandler());
        }

        FluentIterable<? extends RestSetting> handlers = FluentIterable.from(copyOf(settings));
        if (handlers.allMatch(isJsonHandlers())) {
            ImmutableList<Object> objects = handlers.transform(toJsonHandler()).transform(toPojo()).toList();
            this.get(by(uri(resourceRoot(name)))).response(Moco.toJson(objects));
        }

        this.get(InternalApis.context(resourceRoot(name))).response(status(HttpResponseStatus.NOT_FOUND.code()));
    }

    private Function<JsonResponseHandler, Object> toPojo() {
        return new Function<JsonResponseHandler, Object>() {
            @Override
            public Object apply(final JsonResponseHandler handler) {
                return handler.getPojo();
            }
        };
    }

    private Function<RestSetting, JsonResponseHandler> toJsonHandler() {
        return new Function<RestSetting, JsonResponseHandler>() {
            @Override
            public JsonResponseHandler apply(final RestSetting setting) {
                return JsonResponseHandler.class.cast(setting.getHandler());
            }
        };
    }

    private Predicate<RestSetting> isJsonHandlers() {
        return new Predicate<RestSetting>() {
            @Override
            public boolean apply(final RestSetting setting) {
                return setting.getHandler() instanceof JsonResponseHandler;
            }
        };
    }

    @Override
    protected HttpResponseSetting onRequestAttached(final RequestMatcher matcher) {
        HttpSetting baseSetting = new HttpSetting(matcher);
        addSetting(baseSetting);
        return baseSetting;
    }
}
