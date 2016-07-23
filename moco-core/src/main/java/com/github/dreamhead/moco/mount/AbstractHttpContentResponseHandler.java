package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.handler.AbstractContentResponseHandler;
import com.github.dreamhead.moco.model.MessageContent;

public abstract class AbstractHttpContentResponseHandler extends AbstractContentResponseHandler {
    protected abstract MessageContent responseContent(HttpRequest httpRequest);

    @Override
    protected final MessageContent responseContent(final Request request) {
        if (!HttpRequest.class.isInstance(request)) {
            throw new MocoException("Only HTTP request is allowed");
        }

        return responseContent((HttpRequest) request);
    }
}
