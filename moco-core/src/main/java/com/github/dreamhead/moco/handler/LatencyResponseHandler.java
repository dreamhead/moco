package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class LatencyResponseHandler implements ResponseHandler {
    private final long millis;

    public LatencyResponseHandler(long millis) {
        this.millis = millis;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
