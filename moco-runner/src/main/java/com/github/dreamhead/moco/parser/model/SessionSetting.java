package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.handler.SequenceContentHandler;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import static com.github.dreamhead.moco.MocoMount.to;

@JsonIgnoreProperties({"description"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;
    private ResponseSetting[] responses;
    private String redirectTo;
    private MountSetting mount;
    private EventSetting on;
    private ProxyContainer proxy;

    private boolean isMount() {
        return this.mount != null;
    }

    private boolean isAnyResponse() {
        return request == null && mount == null && proxy == null && redirectTo == null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("request", request)
                .add("response", response)
                .add("redirect to", redirectTo)
                .add("mount", mount)
                .add("proxy", proxy)
                .add("on", on)
                .toString();
    }

    private boolean isRedirectResponse() {
        return redirectTo != null;
    }

    private ResponseHandler getResponseHandler() {
        if (response == null && responses == null) {
            throw new IllegalArgumentException("No response or responses specified");
        }

        if (response != null) {
            return response.getResponseHandler();
        }

        return new SequenceContentHandler(getResponseHandlers());
    }

    private ImmutableList<ResponseHandler> getResponseHandlers() {
        ImmutableList.Builder<ResponseHandler> builder = ImmutableList.builder();
        for (ResponseSetting responseSetting : responses) {
            builder.add(responseSetting.getResponseHandler());
        }
        return builder.build();
    }

    private RequestMatcher getRequestMatcher() {
        if (request == null) {
            throw new IllegalArgumentException("No request specified");
        }

        return request.getRequestMatcher();
    }

    public void bindTo(HttpServer server) {
        HttpResponseSetting setting = bindToSession(server);

        if (hasEvent()) {
            for (MocoEventTrigger trigger : on.triggers()) {
                setting.on(trigger);
            }
        }
    }

    public void bindTo(SocketServer server) {
        if (isAnyResponse()) {
            server.response(getResponseHandler());
            return;
        }

        server.request(getRequestMatcher()).response(getResponseHandler());
    }

    private HttpResponseSetting bindToSession(HttpServer server) {
        if (isMount()) {
            return server.mount(mount.getDir(), to(mount.getUri()), mount.getMountPredicates()).response(mount.getResponseHandler());
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

        HttpResponseSetting request = server.request(getRequestMatcher());
        if (isRedirectResponse()) {
            return request.redirectTo(redirectTo);
        }

        return request.response(getResponseHandler());
    }

    private boolean isProxy() {
        return this.proxy != null;
    }

    private boolean hasEvent() {
        return this.on != null;
    }
}
