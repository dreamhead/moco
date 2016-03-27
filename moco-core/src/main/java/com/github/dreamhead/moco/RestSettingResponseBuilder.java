package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface RestSettingResponseBuilder {
    RestSetting response(final ResponseHandler handler, final ResponseHandler... handlers);
    RestSetting response(final String content);
    RestSetting response(final Resource resource);
    RestSetting response(final MocoProcedure procedure);
}
