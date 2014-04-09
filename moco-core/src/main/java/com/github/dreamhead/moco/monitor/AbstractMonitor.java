package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoMonitor;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class AbstractMonitor implements MocoMonitor {
    @Override
    public void onMessageArrived(final HttpRequest request) {
    }

    @Override
    public void onException(final Exception e) {
    }

    @Override
    public void onMessageLeave(final FullHttpResponse response) {
    }

    @Override
    public void onUnexpectedMessage(final HttpRequest request) {
    }
}
