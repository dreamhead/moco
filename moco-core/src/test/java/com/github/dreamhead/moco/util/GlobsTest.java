package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.MocoException;
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GlobsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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

    @Test
    public void should_glob_absolute_files() throws IOException {
        File file = folder.newFile();
        String path = file.getAbsolutePath();
        ImmutableList<String> files = Globs.glob(path);
        assertThat(files.contains(path), is(true));
    }

    @Test
    public void should_glob_absolute_files_with_glob() throws IOException {
        File file = folder.newFile("glob.json");
        String glob = Files.join(folder.getRoot().getAbsolutePath(), "*.json");
        ImmutableList<String> files = Globs.glob(glob);
        assertThat(files.contains(file.getAbsolutePath()), is(true));
    }

    @Test(expected = MocoException.class)
    public void should_throw_exception_for_unknown_root() {
        Globs.glob("unknown/src/test/resources/details/*.json");
    }

    @Test
    public void should_glob_files() {
        ImmutableList<String> glob = Globs.glob("*.json");
        assertThat(glob.isEmpty(), is(true));
    }
}