package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.rest.ActualRestServer;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.util.Iterables.head;
import static com.github.dreamhead.moco.util.Iterables.tail;

@JsonIgnoreProperties({"description"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;
    private TextContainer redirectTo;
    private MountSetting mount;
    private EventSetting on;
    private ProxyContainer proxy;
    private ResourceSetting resource;

    private boolean isMount() {
        return this.mount != null;
    }

    private boolean isAnyResponse() {
        return request == null && mount == null && proxy == null && redirectTo == null && resource == null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("request", request)
                .add("response", response)
                .add("redirect to", redirectTo)
                .add("mount", mount)
                .add("proxy", proxy)
                .add("on", on)
                .add("resource", resource)
                .toString();
    }

    private boolean isRedirectResponse() {
        return redirectTo != null;
    }

    private ResponseHandler getResponseHandler() {
        if (response == null) {
            throw new IllegalArgumentException("No response specified");
        }

        return response.getResponseHandler();
    }

    private RequestMatcher getRequestMatcher() {
        if (request == null) {
            throw new IllegalArgumentException("No request specified");
        }

        return request.getRequestMatcher();
    }

    public void bindTo(final HttpServer server) {
        HttpResponseSetting setting = bindToSession(server);

        if (hasEvent()) {
            for (MocoEventTrigger trigger : on.triggers()) {
                setting.on(trigger);
            }
        }
    }

    public void bindTo(final SocketServer server) {
        if (isAnyResponse()) {
            server.response(getResponseHandler());
            return;
        }

        server.request(getRequestMatcher()).response(getResponseHandler());
    }

    private HttpResponseSetting bindToSession(final HttpServer server) {
        if (isMount()) {
            return server.mount(mount.getDir(), to(mount.getUri()), mount.getMountPredicates())
                    .response(mount.getResponseHandler());
        }

        if (isProxy()) {
            if (proxy.hasUrl()) {
                throw new IllegalArgumentException("It's not allowed to have URL in proxy from server");
            }

            return server.proxy(proxy.getProxyConfig(), proxy.getFailover());
        }

        if (isAnyResponse()) {
            return server.response(getResponseHandler());
        }

        HttpResponseSetting targetRequest = server.request(getRequestMatcher());
        if (isRedirectResponse()) {
            return targetRequest.redirectTo(this.redirectTo.asResource());
        }

        return targetRequest.response(getResponseHandler());
    }

    private boolean isProxy() {
        return this.proxy != null;
    }

    private boolean hasEvent() {
        return this.on != null;
    }

    public boolean isResource() {
        return resource != null;
    }

    public ActualHttpServer newHttpServer(final int port,
                                          final MocoConfig[] configs) {
        if (isResource()) {
            ActualRestServer server = new ActualRestServer(port, null, log(), configs);
            RestSetting[] settings = resource.getSettings();
            server.resource(resource.getName(), head(settings), tail(settings));
            return server;
        }

        ActualHttpServer server = ActualHttpServer.createLogServer(port, configs);
        bindTo(server);
        return server;
    }
}
