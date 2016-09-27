package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.google.common.base.Optional.of;

public abstract class AbstractFileResourceReader implements ContentResourceReader {

    protected abstract byte[] doReadFor(final Optional<? extends Request> request);

    private final Resource filename;
    private final Optional<Charset> charset;

    protected AbstractFileResourceReader(final Resource filename, final Optional<Charset> charset) {
        this.charset = charset;
        this.filename = filename;
    }

    @Override
    public final MessageContent readFor(final Optional<? extends Request> request) {
        return asMessageContent(doReadFor(request));
    }

    private MessageContent asMessageContent(final byte[] content) {
        MessageContent.Builder builder = content().withContent(content);
        if (charset.isPresent()) {
            builder.withCharset(charset.get());
        }

        return builder.build();
    }

    @Override
    public final MediaType getContentType(final HttpRequest request) {
        String targetFilename = this.filename(of(request));
        return new FileContentType(targetFilename, charset).getContentType();
    }

    protected final String filename(final Optional<? extends Request> request) {
        MessageContent messageContent = this.filename.readFor(request);
        return messageContent.toString();
    }
}
