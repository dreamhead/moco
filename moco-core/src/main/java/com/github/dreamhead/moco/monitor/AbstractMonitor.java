package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoMonitor;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class AbstractMonitor implements MocoMonitor {
    @Override
    public void onMessageArrived(HttpRequest request) {
    }

    @Override
    public void onException(Exception e) {
    }

    @Override
    public void onMessageLeave(FullHttpResponse response) {
    }

    @Override
    public void onUnexpectedMessage(FullHttpRequest request) {
    }
}
