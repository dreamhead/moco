package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.resource.Resource;

import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public class PingPongSetting implements PongResponse {
    private final RequestMatcher ping;
    private ResponseHandler pong;

    public PingPongSetting(final RequestMatcher ping) {
        this.ping = ping;
    }

    @Override
    public void pong(final String message) {
        this.pong(text(checkNotNullOrEmpty(message, "Pong message should not be null")));
    }

    @Override
    public void pong(final Resource message) {
        this.pong(with(with(checkNotNull(message, "Pong message should not be null"))));
    }

    @Override
    public void pong(final ResponseHandler message) {
        this.pong = checkNotNull(message, "Pong message should not be null");
    }

    public boolean match(final Request request) {
        return this.ping.match(request);
    }

    public void writeToResponse(final SessionContext context) {
        this.pong.writeToResponse(context);
    }
}
