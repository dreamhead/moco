package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;

public class MocoAsyncAction implements MocoEventAction {
    private static int DEFAULT_LATENCY = 1000;;
    private final MocoEventAction action;

    public MocoAsyncAction(MocoEventAction action) {
        this.action = action;
    }

    @Override
    public void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                waitForAwhile(DEFAULT_LATENCY);
                action.execute();
            }
        }).start();
    }

    private void waitForAwhile(int latency) {
        try {
            Thread.sleep(latency);
        } catch (InterruptedException ignored) {
        }
    }
}
