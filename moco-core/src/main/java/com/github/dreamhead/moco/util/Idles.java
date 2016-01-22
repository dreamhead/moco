package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.MocoException;

import java.util.concurrent.TimeUnit;

public final class Idles {
    public static void idle(final long duration, final TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            throw new MocoException(e);
        }
    }

    private Idles() {
    }
}
