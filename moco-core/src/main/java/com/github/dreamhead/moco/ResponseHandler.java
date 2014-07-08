package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.SessionContext;

public interface ResponseHandler extends ConfigApplier<ResponseHandler> {
    void writeToResponse(final SessionContext context);
}
