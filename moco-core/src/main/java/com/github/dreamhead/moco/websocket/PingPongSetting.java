package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;

import java.util.Arrays;

import static com.github.dreamhead.moco.Moco.text;

public class PingPongSetting implements PongResponse {
    private Resource ping;
    private Resource pong;

    public PingPongSetting(final Resource ping) {
        this.ping = ping;
    }

    @Override
    public void pong(final String pong) {
        this.pong(text(pong));
    }

    @Override
    public void pong(final Resource pong) {
        this.pong = pong;
    }

    public boolean match(final Request request) {
        MessageContent pingContent = this.ping.readFor(null);
        return request.getContent().equals(pingContent);
    }

    public Resource getPong() {
        return pong;
    }
}
