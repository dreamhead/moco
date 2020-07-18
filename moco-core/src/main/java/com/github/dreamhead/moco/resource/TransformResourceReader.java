package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.google.common.net.MediaType;

import java.util.function.Function;

public class TransformResourceReader implements ContentResourceReader {
    private final Function<byte[], byte[]> transformer;
    private final ContentResourceReader reader;

    public TransformResourceReader(final Function<byte[], byte[]> transformer, final ContentResourceReader reader) {
        this.transformer = transformer;
        this.reader = reader;
    }

    @Override
    public MessageContent readFor(Request request) {
        MessageContent messageContent = reader.readFor(request);
        if (transformer == null) {
            return messageContent;
        }

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
