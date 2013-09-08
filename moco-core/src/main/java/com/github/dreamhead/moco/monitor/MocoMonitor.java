package com.github.dreamhead.moco.monitor;

import com.google.common.eventbus.Subscribe;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface MocoMonitor {
    @Subscribe
    void onMessageArrived(FullHttpRequest request);

    @Subscribe
    void onException(Exception e);

    @Subscribe
    void onMessageLeave(FullHttpResponse response);

    MocoMonitor NO_OP_MONITOR = new MocoMonitor() {
        @Override
        public void onMessageArrived(FullHttpRequest request) {
        }

        @Override
        public void onException(Exception e) {
        }

        @Override
        public void onMessageLeave(FullHttpResponse response) {
        }
    };
}
