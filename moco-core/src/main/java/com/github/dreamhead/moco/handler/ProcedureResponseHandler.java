package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoProcedure;
import com.github.dreamhead.moco.ResponseHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class ProcedureResponseHandler implements ResponseHandler {
    private MocoProcedure procedure;

    public ProcedureResponseHandler(MocoProcedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        this.procedure.execute();
    }

    @Override
    public ResponseHandler apply(MocoConfig config) {
        return this;
    }
}
