package com.github.dreamhead.moco.util;

import org.junit.Test;

import static com.github.dreamhead.moco.util.Strings.strip;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringUtilTest {
    @Test
    public void should_strip_null_to_empty_string() {
        assertThat(strip(null), is(""));
    }

    @Test
    public void should_strip_empty_to_empty_string() {
        assertThat(strip(""), is(""));
    }

    @Test
    public void should_strip_ordinary_string_as_it_is() {
        assertThat(strip("foo"), is("foo"));
        assertThat(strip("bar"), is("bar"));
    }

    @Test
    public void should_strip_string_with_leading_whitespace() {
        assertThat(strip(" foo"), is("foo"));
        assertThat(strip("  bar"), is("bar"));
    }

    @Test
    public void should_strip_string_with_end_whitespace() {
        assertThat(strip("foo "), is("foo"));
        assertThat(strip("bar  "), is("bar"));
    }

    @Test
    public void should_strip_string_with_both_leading_and_end_whitespace() {
        assertThat(strip(" foo "), is("foo"));
        assertThat(strip("  bar  "), is("bar"));
    }
}