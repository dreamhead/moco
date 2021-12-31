package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;
import java.util.function.Function;

public final class XmlResourceReader implements ContentResourceReader, FunctionResourceReader {
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
        return this.read(this.function, request);
    }

    @Override
    public MessageContent defaultRead(final Object value) {
        throw new IllegalArgumentException("Unknown xml value:" + value);
    }
}
