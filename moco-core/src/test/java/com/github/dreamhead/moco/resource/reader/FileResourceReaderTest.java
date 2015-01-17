package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.google.common.base.Optional;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileResourceReaderTest {
    @Test
    public void should_return_class_path_file_content() {
        FileResourceReader reader = new FileResourceReader(new File("src/test/resources/foo.response"), Optional.<Charset>absent());
        assertThat(reader.readFor(Optional.<Request>absent()).toString(), is("foo.response"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_file_does_not_exist() {
        FileResourceReader reader = new FileResourceReader(new File("src/test/resources/unknown.response"), Optional.<Charset>absent());
        reader.readFor(Optional.<Request>absent());
    }
}