package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.String.format;

public class ClasspathFileResourceReader extends AbstractFileResourceReader {
    public ClasspathFileResourceReader(final Resource filename, final Optional<Charset> charset) {
        super(filename, charset);
    }

    protected byte[] doReadFor(final Optional<? extends Request> request) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        String actualFilename = this.filename(request);
        URL resource = classLoader.getResource(actualFilename);
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
