package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.github.dreamhead.moco.Moco.text;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileResourceReaderTest {
    @Test
    public void should_return_class_path_file_content() {
        FileResourceReader reader = new FileResourceReader(text(new File("src/test/resources/foo.response").getPath()));
        assertThat(reader.readFor((Request) null).toString(), is("foo.response"));
    }

    @Test
    public void should_throw_exception_when_file_does_not_exist() {
        FileResourceReader reader = new FileResourceReader(text(new File("src/test/resources/unknown.response").getPath()));
        assertThrows(IllegalArgumentException.class, () ->
                reader.readFor((Request) null));
    }
}