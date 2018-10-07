package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

public final class ProcedureResponseHandler extends AbstractResponseHandler implements ResponseHandler {
    private final MocoProcedure procedure;

    public ProcedureResponseHandler(final MocoProcedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        this.procedure.execute();
    }
}
