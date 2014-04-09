package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface ResponseSetting {
    ResponseSetting response(final String content);

    ResponseSetting response(final Resource resource);

    ResponseSetting response(final ResponseHandler handler);

    ResponseSetting response(final MocoProcedure procedure);

    ResponseSetting response(final ResponseHandler... handlers);

    ResponseSetting redirectTo(final String url);

    ResponseSetting on(final MocoEventTrigger trigger);
}
