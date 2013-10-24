package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.procedure.LatencyProcedure;

public class MocoAsyncAction implements MocoEventAction {
    private final MocoEventAction action;
    private final LatencyProcedure procedure;

    public MocoAsyncAction(MocoEventAction action, LatencyProcedure procedure) {
        this.action = action;
        this.procedure = procedure;
    }

    @Override
    public void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                procedure.execute();
                action.execute();
            }
        }).start();
    }
}
