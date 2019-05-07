package com.github.dreamhead.moco.resource.reader;

import org.junit.Test;

import java.io.File;

import static com.github.dreamhead.moco.Moco.text;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileResourceReaderTest {
    @Test
    public void should_return_class_path_file_content() {
        FileResourceReader reader = new FileResourceReader(text(new File("src/test/resources/foo.response").getPath()));
        assertThat(reader.readFor(null).toString(), is("foo.response"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_file_does_not_exist() {
        FileResourceReader reader = new FileResourceReader(text(new File("src/test/resources/unknown.response").getPath()));
        reader.readFor(null);
    }
}