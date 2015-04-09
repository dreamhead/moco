package com.github.dreamhead.moco.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class URLsTest {
    @Test
    public void should_join_path() {
        assertThat(URLs.join("base", "path"), is("base/path"));
        assertThat(URLs.join("base/", "path"), is("base/path"));
        assertThat(URLs.join("base", ""), is("base"));
        assertThat(URLs.join("base", "/path"), is("base/path"));
        assertThat(URLs.join("base/", "/path"), is("base/path"));
    }
}