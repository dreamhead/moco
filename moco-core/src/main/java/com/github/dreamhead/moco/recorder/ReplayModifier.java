package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

public class ReplayModifier implements RecorderConfig, ConfigApplier<ReplayModifier> {
    private ResponseHandler responseHandler;

    public ReplayModifier(final ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public final boolean isFor(final String name) {
        return MODIFIER.equalsIgnoreCase(name);
    }

    public void writeToResponse(final SessionContext context) {
        responseHandler.writeToResponse(context);
    }

    @Override
    public ReplayModifier apply(final MocoConfig config) {
        ResponseHandler applied = this.responseHandler.apply(config);
        if (applied != this.responseHandler) {
            return new ReplayModifier(applied);
        }

        return this;
    }
}
