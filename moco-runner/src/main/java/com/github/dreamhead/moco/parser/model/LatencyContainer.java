package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.parser.deserializer.LatencyContainerDeserializer;
import com.github.dreamhead.moco.procedure.LatencyProcedure;
import com.google.common.base.MoreObjects;

import java.util.concurrent.TimeUnit;

@JsonDeserialize(using = LatencyContainerDeserializer.class)
public final class LatencyContainer {
    private long latency;
    private TimeUnit unit = TimeUnit.MILLISECONDS;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("latency", latency)
                .add("unit", unit)
                .toString();
    }

    public long getLatency() {
        return latency;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public static LatencyContainer latency(final long latency) {
        return latencyWithUnit(latency, TimeUnit.MILLISECONDS);
    }

    public static LatencyContainer latencyWithUnit(final long latency, final TimeUnit timeUnit) {
        LatencyContainer latencyContainer = new LatencyContainer();
        latencyContainer.latency = latency;
        latencyContainer.unit = timeUnit;
        return latencyContainer;
    }

    public LatencyProcedure asProcedure() {
        return Moco.latency(latency, unit);
    }
}
