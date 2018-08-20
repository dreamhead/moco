package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.model.ResponseSetting;

public interface ResponseHandlerFactory {
    ResponseHandler createResponseHandler(ResponseSetting request);
}
