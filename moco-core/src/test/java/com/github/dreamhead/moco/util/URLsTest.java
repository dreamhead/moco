package com.github.dreamhead.moco.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class URLsTest {
    @Test
    public void should_join_path() {
        assertThat(URLs.join("base", "path"), is("base/path"));
        assertThat(URLs.join("base/", "path"), is("base/path"));
        assertThat(URLs.join("base", ""), is("base"));
        assertThat(URLs.join("base", "/path"), is("base/path"));
        assertThat(URLs.join("base/", "/path"), is("base/path"));
        assertThat(URLs.join("base", "path", "sub"), is("base/path/sub"));
    }

    @Test
    public void should_know_valid_url_character() {
        assertThat(URLs.isValidUrl("base"), is(true));
        assertThat(URLs.isValidUrl("base path"), is(false));
    }
}
