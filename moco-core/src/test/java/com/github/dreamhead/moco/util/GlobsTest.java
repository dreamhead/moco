package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GlobsTest {
    @Test
    public void should_glob_relative_files() {
        ImmutableList<String> files = Globs.glob("src/test/resources/details/*.json");
        assertThat(files.contains("src/test/resources/details/foo.json"), is(true));
        assertThat(files.contains("src/test/resources/details/bar.json"), is(true));
    }

    @Test
    public void should_glob_direct_files() {
        ImmutableList<String> files = Globs.glob("src/test/resources/details/foo.json");
        assertThat(files.contains("src/test/resources/details/foo.json"), is(true));
    }

}