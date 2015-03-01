package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.parser.deserializer.LatencyContainerDeserializer;

import java.util.concurrent.TimeUnit;

@JsonDeserialize(using = LatencyContainerDeserializer.class)
public class LatencyContainer {
    private long latency;
    private TimeUnit uint = TimeUnit.MILLISECONDS;

    public long getLatency() {
        return latency;
    }

    public TimeUnit getUint() {
        return uint;
    }

    public static LatencyContainer latency(long latency) {
        LatencyContainer latencyContainer = new LatencyContainer();
        latencyContainer.latency = latency;
        return latencyContainer;
    }

    public static LatencyContainer latencyWithUnit(long latency, TimeUnit timeUnit) {
        LatencyContainer latencyContainer = new LatencyContainer();
        latencyContainer.latency = latency;
        latencyContainer.uint = timeUnit;
        return latencyContainer;
    }
}
