package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface ResponseBase<T> {
    T response(final ResponseHandler handler, final ResponseHandler... handlers);
    T response(final String content);
    T response(final Resource resource);
    T response(final MocoProcedure procedure);
}
