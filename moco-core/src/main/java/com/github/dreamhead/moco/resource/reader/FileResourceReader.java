package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;

public final class FileResourceReader extends AbstractFileResourceReader {
    private final MocoConfig config;

    public FileResourceReader(final Resource file) {
        this(file, null, null);
    }

    public FileResourceReader(final Resource file, final Charset charset, final MocoConfig config) {
        super(file, charset);
        this.config = config;
    }

    @Override
    protected byte[] doReadFor(final Request request) {
        String pathname = targetFileName(request);
        Path path = Paths.get(pathname);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException(format("%s does not exist", path.toString()));
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private String targetFileName(final Request request) {
        String filename = this.filename(request);

        if (config != null) {
            return (String) config.apply(filename);
        }

        return filename;
    }
}
