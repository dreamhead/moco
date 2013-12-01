package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

public class ProcedureResponseHandler implements ResponseHandler {
    private MocoProcedure procedure;

    public ProcedureResponseHandler(MocoProcedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        this.procedure.execute();
    }

    @Override
    public ResponseHandler apply(MocoConfig config) {
        return this;
    }
}
