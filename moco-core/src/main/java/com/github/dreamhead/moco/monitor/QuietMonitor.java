package com.github.dreamhead.moco.monitor;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuietMonitor implements MocoMonitor {
    private static Logger logger = LoggerFactory.getLogger(QuietMonitor.class);

    @Override
    public void onMessageArrived(FullHttpRequest request) {
    }

    @Override
    public void onException(Exception e) {
        logger.error("Exception thrown", e);
    }

    @Override
    public void onMessageLeave(FullHttpResponse response) {
    }
}
