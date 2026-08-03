package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.util.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public final class ClasspathFileResourceReader extends AbstractFileResourceReader {
    public ClasspathFileResourceReader(final Resource filename, final Charset charset) {
        super(filename, charset);
    }

    protected byte[] doReadFor(final Request request) {
        String actualFilename = this.filename(request);
        URL resource = Resources.getResource(actualFilename);
        if (resource == null) {
            throw new IllegalArgumentException("%s does not exist".formatted(actualFilename));
        }
        try {
            return resource.openStream().readAllBytes();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }
}
