package com.github.dreamhead.moco.util;

import java.util.concurrent.TimeUnit;

public class Idles {
    public static void idle(long idle) {
        idle(idle, TimeUnit.MILLISECONDS);
    }

    public static void idle(long duration, TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Idles() {}
}
