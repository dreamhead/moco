package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.String.format;

public final class ClasspathFileResourceReader extends AbstractFileResourceReader {
    public ClasspathFileResourceReader(final Resource filename, final Charset charset) {
        super(filename, charset);
    }

    protected byte[] doReadFor(final Request request) {
        String actualFilename = this.filename(request);
        URL resource = Resources.getResource(actualFilename);
        if (resource == null) {
            throw new IllegalArgumentException(format("%s does not exist", actualFilename));
        }
        try {
            return toByteArray(resource.openStream());
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }
}
