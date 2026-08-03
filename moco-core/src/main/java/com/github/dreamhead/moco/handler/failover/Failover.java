package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private Set<Integer> asProxyStatuses(final int[] proxyStatuses) {
        if (proxyStatuses.length == 0) {
            return Set.of(HttpResponseStatus.BAD_REQUEST.code());
        }

        // Collected rather than Set.of(...), which would reject the repeated status in
        // failover(file, 400, 400) where Guava's ImmutableSet.copyOf silently de-duplicated.
        return IntStream.of(proxyStatuses).boxed().collect(Collectors.toUnmodifiableSet());
    }

    public boolean shouldFailover(final int statusCode) {
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

    public boolean hasFailover() {
        return executor != FailoverExecutor.EMPTY_FAILOVER;
    }
}
