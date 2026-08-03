package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FilesTest {
    @Test
    public void should_get_extension() {
        assertThat(Files.getFileExtension("logo.png"), is("png"));
        assertThat(Files.getFileExtension("archive.tar.gz"), is("gz"));
        assertThat(Files.getFileExtension("/a/b/logo.PNG"), is("PNG"));
    }

    @Test
    public void should_get_empty_extension_when_there_is_no_dot() {
        // FileContentType lower-cases the result, so it must never be null.
        assertThat(Files.getFileExtension("UNKNOWN_FILE"), is(""));
        assertThat(Files.getFileExtension(""), is(""));
    }

    @Test
    public void should_ignore_dot_in_parent_directory() {
        assertThat(Files.getFileExtension("foo.bar/README"), is(""));
        assertThat(Files.getFileExtension("foo.bar/logo.png"), is("png"));
    }

    @Test
    public void should_treat_dotfile_name_as_extension() {
        assertThat(Files.getFileExtension(".gitignore"), is("gitignore"));
    }
}
