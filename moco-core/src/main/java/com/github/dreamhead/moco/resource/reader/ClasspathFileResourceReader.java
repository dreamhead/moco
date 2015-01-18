package com.github.dreamhead.moco.resource.reader;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.String.format;

public class ClasspathFileResourceReader extends AbstractFileResourceReader {
    public ClasspathFileResourceReader(String filename, Optional<Charset> charset) {
        super(charset, filename);
    }

    protected byte[] doReadFor() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(filename);
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
