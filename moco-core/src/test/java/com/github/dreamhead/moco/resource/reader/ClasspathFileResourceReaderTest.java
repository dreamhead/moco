package com.github.dreamhead.moco.resource.reader;

import org.junit.Test;

import static com.github.dreamhead.moco.Moco.text;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClasspathFileResourceReaderTest {
    @Test
    public void should_return_class_path_file_content() {
        ClasspathFileResourceReader reader = new ClasspathFileResourceReader(text("foo.response"), null);
        assertThat(reader.readFor(null).toString(), is("foo.response"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_file_does_not_exist() {
        ClasspathFileResourceReader reader = new ClasspathFileResourceReader(text("unknown.response"), null);
        reader.readFor(null);
    }
}
