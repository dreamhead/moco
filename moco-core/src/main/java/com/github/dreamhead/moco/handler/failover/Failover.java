package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Set;

public final class Failover {
    public static final Failover DEFAULT_FAILOVER = new Failover(FailoverExecutor.EMPTY_FAILOVER,
            FailoverStrategy.FAILOVER);

    private final FailoverExecutor executor;
    private final FailoverStrategy strategy;
    private final Set<Integer> statuses;

    public Failover(final FailoverExecutor executor, final FailoverStrategy strategy, final int... statuses) {
        this.executor = executor;
        this.strategy = strategy;
        this.statuses = asProxyStatuses(statuses);
    }

    private ImmutableSet<Integer> asProxyStatuses(final int[] proxyStatuses) {
        if (proxyStatuses.length == 0) {
            return ImmutableSet.of(HttpResponseStatus.BAD_REQUEST.code());
        }

        return ImmutableSet.copyOf(Ints.asList(proxyStatuses));
    }

    public boolean shouldFailover(final org.apache.http.HttpResponse remoteResponse) {
        int statusCode = remoteResponse.getStatusLine().getStatusCode();
        return statuses.contains(statusCode);
    }

    public HttpResponse failover(final HttpRequest request) {
        return executor.failover(request);
    }

    public void onCompleteResponse(final HttpRequest request, final HttpResponse response) {
        executor.onCompleteResponse(request, response);
    }

    public boolean isPlayback() {
        return strategy == FailoverStrategy.PLAYBACK;
    }
}
