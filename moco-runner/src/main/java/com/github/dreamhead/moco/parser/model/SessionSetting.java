package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.dreamhead.moco.HttpResponseSetting;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.handler.SequenceContentHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.toVariables;
import static com.google.common.base.Preconditions.checkArgument;

@JsonIgnoreProperties({"description"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SessionSetting {
    private RequestSetting request;
    private ResponseSetting response;
    private TextContainer redirectTo;
    private MountSetting mount;
    private EventSetting on;
    private ProxyContainer proxy;
    private ResponseSetting[] responseSeq;

    private boolean isMount() {
        return this.mount != null;
    }

    private boolean isAnyResponse() {
        return request == null && mount == null && proxy == null && redirectTo == null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("request", request)
                .add("response", response)
                .add("responseSeq", responseSeq)
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
        if (response == null && responseSeq == null) {
            throw new IllegalArgumentException("No response specified");
        }

        if (response != null && responseSeq != null) {
            throw new IllegalArgumentException("response and responseSeq can not be specified at same time, choose either of two");
        }
        if (response != null) {
            return response.getResponseHandler();
        }
        return getSeqResponseHandler();
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
            return targetRequest.redirectTo(redirectResource(this.redirectTo));
        }

        return targetRequest.response(getResponseHandler());
    }

    private Resource redirectResource(final TextContainer textContainer) {
        if (textContainer.isRawText()) {
            return text(textContainer.getText());
        }

        if (textContainer.isForTemplate()) {
            if (textContainer.hasProperties()) {
                return template(textContainer.getText(), toVariables(textContainer.getProps()));
            }

            return template(textContainer.getText());
        }

        throw new IllegalArgumentException("Illegal resource" + textContainer);
    }

    private boolean isProxy() {
        return this.proxy != null;
    }

    private boolean hasEvent() {
        return this.on != null;
    }

    private void checkSeqResponse() {
        checkArgument(responseSeq != null, "seq contents should not be null");
        checkArgument(responseSeq.length > 0, "seq contents should contain at least one response");
    }

    private ResponseHandler getSeqResponseHandler() {
        checkSeqResponse();

        ImmutableList.Builder<ResponseHandler> builder = ImmutableList.builder();
        for (ResponseSetting responseSetting : responseSeq) {
            builder.add(responseSetting.getResponseHandler());
        }

        return new SequenceContentHandler(builder.build());
    }
}
