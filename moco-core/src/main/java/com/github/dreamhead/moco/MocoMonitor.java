package com.github.dreamhead.moco;

import com.google.common.eventbus.Subscribe;
import io.netty.handler.codec.http.FullHttpResponse;

public interface MocoMonitor {
    @Subscribe
    void onMessageArrived(HttpRequest request);

    @Subscribe
    void onException(Exception e);

    @Subscribe
    void onMessageLeave(FullHttpResponse response);

    @Subscribe
    void onUnexpectedMessage(HttpRequest request);
}
