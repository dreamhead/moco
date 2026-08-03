package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.MocoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GlobsTest {

    @Test
    public void should_glob_relative_files() {
        List<String> files = Globs.glob("src/test/resources/details/*.json");
        assertThat(files.contains("src/test/resources/details/foo.json"), is(true));
        assertThat(files.contains("src/test/resources/details/bar.json"), is(true));
    }

    @Test
    public void should_glob_direct_files() {
        List<String> files = Globs.glob("src/test/resources/details/foo.json");
        assertThat(files.contains("src/test/resources/details/foo.json"), is(true));
    }

    @Test
    public void should_glob_absolute_files(@TempDir final File folder) {
        File file = new File(folder, "glob-absolute.json");
        String path = file.getAbsolutePath();
        List<String> files = Globs.glob(path);
        assertThat(files.contains(path), is(true));
    }

    @Test
    public void should_glob_absolute_files_with_glob(@TempDir final Path folder) throws IOException {
        Path tempFile = folder.resolve("glob.json");

        java.nio.file.Files.createFile(tempFile);
        File file = tempFile.toFile();
        String glob = Files.join(folder.toFile().getAbsolutePath(), "*.json");
        List<String> files = Globs.glob(glob);
        assertThat(files.contains(file.getAbsolutePath()), is(true));
    }

    @Test
    public void should_throw_exception_for_unknown_root() {
        assertThrows(MocoException.class, () -> {
            Globs.glob("unknown/src/test/resources/details/*.json");
        });
    }

    @Test
    public void should_glob_files() {
        List<String> glob = Globs.glob("*.json");
        assertThat(glob.isEmpty(), is(true));
    }
}