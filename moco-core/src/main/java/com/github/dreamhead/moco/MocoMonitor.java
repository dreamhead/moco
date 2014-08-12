package com.github.dreamhead.moco;

import com.google.common.eventbus.Subscribe;

public interface MocoMonitor {
    @Subscribe
    void onMessageArrived(final Request request);

    @Subscribe
    void onException(final Exception e);

    @Subscribe
    void onMessageLeave(final Response response);

    @Subscribe
    void onUnexpectedMessage(final Request request);
}
