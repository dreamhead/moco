package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.google.common.base.Optional;
import org.junit.Test;

import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.text;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClasspathFileResourceReaderTest {
    @Test
    public void should_return_class_path_file_content() {
        ClasspathFileResourceReader reader = new ClasspathFileResourceReader(text("foo.response"), Optional.<Charset>absent());
        assertThat(reader.readFor(Optional.<Request>absent()).toString(), is("foo.response"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_file_does_not_exist() {
        ClasspathFileResourceReader reader = new ClasspathFileResourceReader(text("unknown.response"), Optional.<Charset>absent());
        reader.readFor(Optional.<Request>absent());
    }
}
