package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.model.MessageContent;

public interface ResourceReader {
    MessageContent readFor(Request request);

    default MessageContent readFor(SessionContext context) {
        if (context == null) {
            return this.readFor((Request) null);
        }
        return this.readFor(context.getRequest());
    }
}
