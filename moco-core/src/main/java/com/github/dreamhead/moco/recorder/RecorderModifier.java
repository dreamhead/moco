package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

public class RecorderModifier implements RecorderConfig {
    private ResponseHandler responseHandler;

    public RecorderModifier(final ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public final boolean isFor(final String name) {
        return MODIFIER.equalsIgnoreCase(name);
    }

    public void writeToResponse(final SessionContext context) {
        responseHandler.writeToResponse(context);

    }
}
