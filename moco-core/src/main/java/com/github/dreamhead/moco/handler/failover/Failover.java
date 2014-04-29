package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

public class Failover {
    public static final Failover DEFAULT_FAILOVER = new Failover(FailoverExecutor.EMPTY_FAILOVER, FailoverStrategy.FAILOVER);

    private FailoverExecutor executor;
    private FailoverStrategy strategy;

    public Failover(FailoverExecutor executor, FailoverStrategy strategy) {
        this.executor = executor;
        this.strategy = strategy;
    }

    public FailoverStrategy getStrategy() {
        return strategy;
    }

    public void failover(HttpRequest request, FullHttpResponse response) {
        executor.failover(request, response);
    }

    public void onCompleteResponse(HttpRequest request, HttpResponse httpResponse) {
        executor.onCompleteResponse(request, httpResponse);
    }
}
