package com.github.dreamhead.moco;

import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.eventbus.Subscribe;

public interface MocoMonitor {
    @Subscribe
    void onMessageArrived(Request request);

    @Subscribe
    void onException(Throwable t);

    @Subscribe
    void onMessageLeave(Response response);

    @Subscribe
    void onUnexpectedMessage(Request request);

    void onEvent(SseEvent event);

    boolean isQuiet();
}
