package com.github.dreamhead.moco;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MocoCacheStandaloneTest extends AbstractMocoStandaloneTest {

    private final File cacheTarget = new File("src/test/resources/cache/cache.response");
    private File persistenceTarget;

    @Override
    public void setup() throws IOException {
        super.setup();
        cacheTarget.createNewFile();
        persistenceTarget = new File("src/test/resources/cache/cache.persistence");
        persistenceTarget.createNewFile();
    }

    @Test
    public void should_cache_response() throws IOException {
        runWithConfiguration("cache.json");

        String content = "response from cache";
        changeFileContent(cacheTarget, content);
        assertThat(helper.get(root()), is(content));

        changeFileContent(cacheTarget, "change response");
        assertThat(helper.get(root()), is(content));
    }

    @Test
    public void should_cache_with_persistence_file() throws IOException {
        runWithConfiguration("cache.json");
        String content = "response from cache";

        changeFileContent(cacheTarget, content);
        assertThat(helper.get(remoteUrl("/persist")), is(content));
        assertThat(Files.toString(persistenceTarget, Charset.defaultCharset()), is(content));
    }

    private void changeFileContent(File response, String content) throws FileNotFoundException {
        PrintStream stream = new PrintStream(new FileOutputStream(response));
        stream.print(content);
    }
}
