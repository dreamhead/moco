package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public abstract class AbstractMonitor implements MocoMonitor {
    @Override
    public void onMessageArrived(final Request request) {
    }

    @Override
    public void onException(final Throwable t) {
    }

    @Override
    public void onMessageLeave(final Response response) {
    }

    @Override
    public void onUnexpectedMessage(final Request request) {
    }
}
