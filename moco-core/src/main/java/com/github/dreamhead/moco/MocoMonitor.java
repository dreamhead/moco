package com.github.dreamhead.moco;

import com.google.common.eventbus.Subscribe;
import io.netty.handler.codec.http.FullHttpResponse;

public interface MocoMonitor {
    @Subscribe
    void onMessageArrived(final Request request);

    @Subscribe
    void onException(final Exception e);

    @Subscribe
    void onMessageLeave(final FullHttpResponse response);

    @Subscribe
    void onUnexpectedMessage(final Request request);
}
