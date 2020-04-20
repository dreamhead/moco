package com.github.dreamhead.moco.websocket;

public class PingPongSetting implements PongResponse {
    private String ping;
    private String pong;

    public PingPongSetting(final String ping) {
        this.ping = ping;
    }

    @Override
    public void pong(final String pong) {
        this.pong = pong;
    }

    public String getPing() {
        return ping;
    }

    public String getPong() {
        return pong;
    }
}
