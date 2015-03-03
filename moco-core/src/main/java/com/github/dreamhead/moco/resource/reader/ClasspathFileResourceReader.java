package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.String.format;

public class ClasspathFileResourceReader extends AbstractFileResourceReader {
    public ClasspathFileResourceReader(Resource filename, Optional<Charset> charset) {
        super(filename, charset);
    }

    protected byte[] doReadFor(final Optional<? extends Request> request) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(filename.readFor(request).toString());
        if (resource == null) {
            throw new IllegalArgumentException(format("%s does not exist", filename));
        }
        try {
            return toByteArray(resource.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
