package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Files.toByteArray;
import static java.lang.String.format;

public class FileResourceReader extends AbstractFileResourceReader {
    private Optional<MocoConfig> config;

    public FileResourceReader(Resource file, Optional<Charset> charset) {
        this(file, charset, Optional.<MocoConfig>absent());
    }

    public FileResourceReader(Resource file, Optional<Charset> charset, Optional<MocoConfig> config) {
        super(file, charset);
        this.config = config;

    }

    @Override
    protected byte[] doReadFor(final Optional<? extends Request> request) {
        File file = new File(targetFileName(request));

        if (!file.exists()) {
            throw new IllegalArgumentException(format("%s does not exist", file.getPath()));
        }

        try {
            return toByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private String targetFileName(Optional<? extends Request> request) {
        MessageContent messageContent = filename.readFor(request);
        String filename = messageContent.toString();

        if (config.isPresent()) {
            return (String)config.get().apply(filename);
        }

        return filename;
    }
}
