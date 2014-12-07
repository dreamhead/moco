package com.github.dreamhead.moco.procedure;

import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.util.Idles;

import java.util.concurrent.TimeUnit;

public class LatencyProcedure implements MocoProcedure {
    public static final int DEFAULT_LATENCY = 1000;

    private final long duration;
    private final TimeUnit unit;

    public LatencyProcedure(final long duration, final TimeUnit unit) {
        this.duration = duration;
        this.unit = unit;
    }

    @Override
    public void execute() {
        Idles.idle(duration, unit);
    }
}
