package com.github.dreamhead.moco.util;

public class Idles {
    public static void idle(long idle) {
        try {
            Thread.sleep(idle);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Idles() {}
}
