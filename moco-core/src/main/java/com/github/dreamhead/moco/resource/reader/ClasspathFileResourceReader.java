package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.String.format;

public class ClasspathFileResourceReader implements ContentResourceReader {
    private final String filename;
    private final Optional<Charset> charset;

    public ClasspathFileResourceReader(String filename, Optional<Charset> charset) {
        this.filename = filename;
        this.charset = charset;
    }

    @Override
    public MessageContent readFor(final Optional<? extends Request> request) {
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(filename);
            if (resource == null) {
                throw new IllegalArgumentException(format("%s does not exist", filename));
            }

            MessageContent.Builder builder = content().withContent(toByteArray(resource.openStream()));
            if (charset.isPresent()) {
                builder.withCharset(charset.get());
            }

            return builder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        return new FileContentType(this.filename).getContentType();
    }
}
