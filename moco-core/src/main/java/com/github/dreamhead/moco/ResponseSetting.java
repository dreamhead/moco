package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface ResponseSetting<T extends ResponseSetting> {
    T response(String content);

    T response(Resource resource);

    T response(ResponseHandler handler);

    T response(MocoProcedure procedure);

    T response(ResponseHandler... handlers);

    T on(MocoEventTrigger trigger);
}
