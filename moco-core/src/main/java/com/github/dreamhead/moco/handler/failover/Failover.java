package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;

public class Failover {
    public static final Failover DEFAULT_FAILOVER = new Failover(FailoverExecutor.EMPTY_FAILOVER,
            FailoverStrategy.FAILOVER);

    private final FailoverExecutor executor;
    private final FailoverStrategy strategy;

    public Failover(final FailoverExecutor executor, final FailoverStrategy strategy) {
        this.executor = executor;
        this.strategy = strategy;
    }

    public HttpResponse failover(final HttpRequest request) {
        return executor.failover(request);
    }

    public void onCompleteResponse(final HttpRequest request, final HttpResponse httpResponse) {
        executor.onCompleteResponse(request, httpResponse);
    }

    public boolean isPlayback() {
        return strategy == FailoverStrategy.PLAYBACK;
    }
}
