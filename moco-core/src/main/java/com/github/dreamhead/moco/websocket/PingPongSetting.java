package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.MutableResponse;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;

import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public class PingPongSetting implements PongResponse {
    private Resource ping;
    private Resource pong;

    public PingPongSetting(final Resource ping) {
        this.ping = ping;
    }

    @Override
    public void pong(final String message) {
        this.pong(text(checkNotNullOrEmpty(message, "Pong message should not be null")));
    }

    @Override
    public void pong(final Resource message) {
        this.pong = checkNotNull(message, "Pong message should not be null");
    }

    public boolean match(final Request request) {
        MessageContent pingContent = this.ping.readFor(null);
        return request.getContent().equals(pingContent);
    }

    public void writeToResponse(final SessionContext context) {
        MessageContent pongContent = this.pong.readFor(null);
        Response response = context.getResponse();
        if (MutableResponse.class.isInstance(response)) {
            MutableResponse mutableResponse = (MutableResponse) response;
            mutableResponse.setContent(pongContent);
        }
    }
}
