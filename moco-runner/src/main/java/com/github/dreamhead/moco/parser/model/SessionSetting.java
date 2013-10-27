package com.github.dreamhead.moco.parser.model;

import static com.github.dreamhead.moco.MocoMount.to;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Objects;

@JsonIgnoreProperties({"description"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;
    private String redirectTo;
    private MountSetting mount;
    private EventSetting on;

    private boolean isMount() {
        return this.mount != null;
    }

    private boolean isAnyResponse() {
        return request == null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("request", request).add("response", response).toString();
    }

    private boolean isRedirectResponse() {
        return redirectTo != null;
    }

    private ResponseHandler getResponseHandler() {
        if (response == null) {
            throw new RuntimeException("No response specified");
        }

        return response.getResponseHandler();
    }

    private RequestMatcher getRequestMatcher() {
        if (request == null) {
            throw new RuntimeException("No request specified");
        }

        return request.getRequestMatcher();
    }

    public void bindTo(HttpServer server) {
        com.github.dreamhead.moco.ResponseSetting setting = bindToSession(server);

        if (hasEvent()) {
            for (MocoEventTrigger trigger : on.createTriggers()) {
                setting.on(trigger);
            }
        }
    }

    private com.github.dreamhead.moco.ResponseSetting bindToSession(HttpServer server) {
        if (isMount()) {
            return server.mount(mount.getDir(), to(mount.getUri()), mount.getMountPredicates());
        }

        if (isAnyResponse()) {
            return server.response(getResponseHandler());
        }

        if (isRedirectResponse()) {
            return  server.request(getRequestMatcher()).redirectTo(redirectTo);
        }

        return server.request(getRequestMatcher()).response(getResponseHandler());
    }

    private boolean hasEvent() {
        return this.on != null;
    }
}
