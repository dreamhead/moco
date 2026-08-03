package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.github.dreamhead.moco.util.MediaType;

import java.util.function.Function;

public record TransformResourceReader(Function<byte[], byte[]> transformer, ContentResourceReader reader)
        implements ContentResourceReader {
    @Override
    public MessageContent readFor(final Request request) {
        MessageContent messageContent = reader.readFor(request);
        byte[] transformed = transformer.apply(messageContent.getContent());
        return MessageContent.content()
                .withCharset(messageContent.getCharset())
                .withContent(transformed)
                .build();
    }

    @Override
    public MediaType getContentType(final HttpRequest request) {
        return this.reader.getContentType(request);
    }
}
