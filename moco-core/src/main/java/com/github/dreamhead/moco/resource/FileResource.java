package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.io.Files;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;
import java.io.IOException;

import static com.google.common.io.Files.toByteArray;

public class FileResource implements WritableResource, ContentResource {
    private final File file;

    public FileResource(File file) {
        this.file = file;
    }

    @Override
    public String id() {
        return "file";
    }

    @Override
    public byte[] asByteArray(HttpRequest request) {
        try {
            return toByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource apply(final MocoConfig config) {
        if (config.isFor(this.id())) {
           return new FileResource(new File(config.apply(file.getName())));
        }
        return this;
    }

    @Override
    public void write(byte[] content) {
        try {
            Files.write(content, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        return new FileContentType(file.getName()).getContentType();
    }
}
