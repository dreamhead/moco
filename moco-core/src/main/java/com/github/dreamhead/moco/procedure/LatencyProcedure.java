package com.github.dreamhead.moco.procedure;

import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.util.Idles;

public class LatencyProcedure implements MocoProcedure {
    private final long millis;

    public LatencyProcedure(long millis) {
        this.millis = millis;
    }

    @Override
    public void execute() {
        Idles.idle(millis);
    }
}
