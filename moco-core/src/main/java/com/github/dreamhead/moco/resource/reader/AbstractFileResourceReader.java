package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;

public abstract class AbstractFileResourceReader implements ContentResourceReader {

    protected abstract byte[] doReadFor(final Optional<? extends Request> request);

    protected final String filename;
    protected final Optional<Charset> charset;

    public AbstractFileResourceReader(String filename, Optional<Charset> charset) {
        this.charset = charset;
        this.filename = filename;
    }

    @Override
    public MessageContent readFor(final Optional<? extends Request> request) {
        return asMessageContent(doReadFor(request));
    }

    private MessageContent asMessageContent(byte[] content) {
        MessageContent.Builder builder = content().withContent(content);
        if (charset.isPresent()) {
            builder.withCharset(charset.get());
        }

        return builder.build();
    }

    @Override
    public String getContentType() {
        return new FileContentType(this.filename).getContentType();
    }
}
