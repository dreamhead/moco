package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.net.MediaType;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;

public abstract class AbstractFileResourceReader implements ContentResourceReader {

    protected abstract byte[] doReadFor(Request request);

    private final Resource filename;
    private final Charset charset;

    protected AbstractFileResourceReader(final Resource filename, final Charset charset) {
        this.charset = charset;
        this.filename = filename;
    }

    @Override
    public final MessageContent readFor(final Request request) {
        return asMessageContent(doReadFor(request));
    }

    private MessageContent asMessageContent(final byte[] content) {
        MessageContent.Builder builder = content().withContent(content);
        if (charset != null) {
            builder.withCharset(charset);
        }

        return builder.build();
    }

    @Override
    public final MediaType getContentType(final HttpRequest request) {
        String targetFilename = this.filename(request);
        return new FileContentType(targetFilename, charset).getContentType();
    }

    protected final String filename(final Request request) {
        MessageContent messageContent = this.filename.readFor(request);
        return messageContent.toString();
    }
}
