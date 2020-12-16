package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.recorder.MocoGroup;

public class JoinResponseHandler implements ResponseHandler {
    private MocoGroup group;

    public JoinResponseHandler(final MocoGroup group) {
        this.group = group;
    }

    @Override
    public ResponseHandler apply(MocoConfig config) {
        return null;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        context.join(group);
    }
}
