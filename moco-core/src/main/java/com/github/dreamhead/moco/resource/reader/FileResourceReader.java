package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.google.common.io.Files.toByteArray;
import static java.lang.String.format;

public class FileResourceReader implements ContentResourceReader {
    private final File file;
    private Optional<Charset> charset;

    public FileResourceReader(File file, Optional<Charset> charset) {
        this.file = file;
        this.charset = charset;
    }

    @Override
    public String getContentType() {
        return new FileContentType(file.getName()).getContentType();
    }

    @Override
    public MessageContent readFor(final Optional<? extends Request> request) {
        if (!file.exists()) {
            throw new IllegalArgumentException(format("%s does not exist", file.getPath()));
        }

        try {
            MessageContent.Builder builder = content().withContent(toByteArray(file));
            if (charset.isPresent()) {
                builder.withCharset(charset.get());
            }
            return builder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
