package com.github.dreamhead.moco.procedure;

import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.util.Idles;

import java.util.concurrent.TimeUnit;

public final class LatencyProcedure implements MocoProcedure {
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
