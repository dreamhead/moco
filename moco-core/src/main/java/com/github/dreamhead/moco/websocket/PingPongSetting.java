package com.github.dreamhead.moco.websocket;

import com.github.dreamhead.moco.resource.Resource;

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

    public Resource getPing() {
        return ping;
    }

    public Resource getPong() {
        return pong;
    }
}
