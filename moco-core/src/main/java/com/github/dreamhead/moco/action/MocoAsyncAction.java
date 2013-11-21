package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.procedure.LatencyProcedure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MocoAsyncAction implements MocoEventAction {
    private final MocoEventAction action;
    private final LatencyProcedure procedure;
    private final ExecutorService service = Executors.newCachedThreadPool();

    public MocoAsyncAction(MocoEventAction action, LatencyProcedure procedure) {
        this.action = action;
        this.procedure = procedure;
    }

    @Override
    public void execute() {
        service.execute(new Runnable() {
            @Override
            public void run() {
                procedure.execute();
                action.execute();
            }
        });
    }
}
