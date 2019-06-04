package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;

public interface ResponseBase<T> {
    T response(ResponseHandler handler, ResponseHandler... handlers);
    T response(String content);
    T response(Resource resource);
    T response(MocoProcedure procedure);
    T response(HttpHeader header);
}
