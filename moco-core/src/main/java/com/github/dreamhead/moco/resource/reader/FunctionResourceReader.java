package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;

import java.io.InputStream;
import java.util.function.Function;

import static com.github.dreamhead.moco.util.Functions.checkApply;

public interface FunctionResourceReader {
    MessageContent defaultRead(Object value);

    default MessageContent read(final Function<Request, Object> function, final Request request) {
        Object value = checkApply(function, request);
        if (value instanceof String) {
            return MessageContent.content((String) value);
        }

        if (value instanceof Resource) {
            Resource resource = (Resource) value;
            return resource.readFor(request);
        }

        if (value instanceof InputStream) {
            return MessageContent.content().withContent((InputStream) value).build();
        }

        return this.defaultRead(value);
    }
}
