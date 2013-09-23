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
}
