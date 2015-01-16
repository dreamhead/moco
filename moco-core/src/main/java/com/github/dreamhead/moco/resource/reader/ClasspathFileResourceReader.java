package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.String.format;

public class ClasspathFileResourceReader implements ContentResourceReader {
    private final String filename;

    public ClasspathFileResourceReader(String filename) {
        this.filename = filename;
    }

    @Override
    public MessageContent readFor(final Optional<? extends Request> request) {
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(filename);
            if (resource == null) {
                throw new IllegalArgumentException(format("%s does not exist", filename));
            }

            return content().withContent(toByteArray(resource.openStream())).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        return new FileContentType(this.filename).getContentType();
    }
}
