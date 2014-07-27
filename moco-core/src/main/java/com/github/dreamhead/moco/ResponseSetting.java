package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface ResponseSetting {
    ResponseSetting response(String content);

    ResponseSetting response(Resource resource);

    ResponseSetting response(ResponseHandler handler);

    ResponseSetting response(MocoProcedure procedure);

    ResponseSetting response(ResponseHandler... handlers);

    ResponseSetting on(MocoEventTrigger trigger);
}
