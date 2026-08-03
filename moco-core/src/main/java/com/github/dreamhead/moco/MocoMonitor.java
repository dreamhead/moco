package com.github.dreamhead.moco;

import com.github.dreamhead.moco.sse.SseEvent;

public interface MocoMonitor {
    void onMessageArrived(Request request);

    void onException(Throwable t);

    void onMessageLeave(Response response);

    void onUnexpectedMessage(Request request);

    void onEvent(SseEvent event);

    boolean isQuiet();
}
