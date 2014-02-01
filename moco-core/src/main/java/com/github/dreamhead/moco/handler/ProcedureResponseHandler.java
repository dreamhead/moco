package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

public class ProcedureResponseHandler extends AbstractResponseHandler implements ResponseHandler {
    private MocoProcedure procedure;

    public ProcedureResponseHandler(MocoProcedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        this.procedure.execute();
    }
}
