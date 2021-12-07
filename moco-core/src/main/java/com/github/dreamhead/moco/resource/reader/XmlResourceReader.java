package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.net.MediaType;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Function;

import static com.github.dreamhead.moco.util.Functions.checkApply;

public class XmlResourceReader implements ContentResourceReader {
    private final Function<Request, Object> function;

    public XmlResourceReader(final Function<Request, Object> function) {
        this.function = function;
    }

    @Override
    public MediaType getContentType(final HttpRequest request) {
        return MediaType.create("application", "xml").withCharset(Charset.defaultCharset());
    }

    @Override
    public MessageContent readFor(final Request request) {
        Object value = checkApply(this.function, request);
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

        throw new IllegalArgumentException("Unknown xml value:" + value);
    }
}
