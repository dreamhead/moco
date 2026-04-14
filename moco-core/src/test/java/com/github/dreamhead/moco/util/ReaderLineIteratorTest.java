package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReaderLineIteratorTest {

    @Test
    public void should_iterate_lines() {
        Reader reader = new StringReader("line1\nline2\nline3\n");
        Iterator<String> iterator = new ReaderLineIterator(reader);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is("line1"));
        assertThat(iterator.next(), is("line2"));
        assertThat(iterator.next(), is("line3"));
        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void should_iterate_lines_with_blank_lines() {
        Reader reader = new StringReader("line1\n\nline2\n");
        Iterator<String> iterator = new ReaderLineIterator(reader);

        assertThat(iterator.next(), is("line1"));
        assertThat(iterator.next(), is(""));
        assertThat(iterator.next(), is("line2"));
        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void should_handle_empty_reader() {
        Reader reader = new StringReader("");
        Iterator<String> iterator = new ReaderLineIterator(reader);

        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void should_throw_on_next_when_exhausted() {
        Reader reader = new StringReader("line1\n");
        Iterator<String> iterator = new ReaderLineIterator(reader);

        iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void should_not_require_trailing_newline() {
        Reader reader = new StringReader("line1\nline2");
        Iterator<String> iterator = new ReaderLineIterator(reader);

        assertThat(iterator.next(), is("line1"));
        assertThat(iterator.next(), is("line2"));
        assertThat(iterator.hasNext(), is(false));
    }
}
