package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoCacheTest {
    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        server = httpserver(port());
    }

    @Test
    public void should_change_file_content_dynamically() throws IOException {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");

        server.response(file(response.getAbsolutePath()));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo"));
                    changeFileContent(response, "bar");
                    assertThat(helper.get(root()), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Test
    public void should_cache_result() throws IOException {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");

        server.response(MocoCache.cache(file(response.getAbsolutePath())));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo"));
                    changeFileContent(response, "bar");
                    assertThat(helper.get(root()), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_cache_with_persistence_file() throws IOException {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");
        final File cacheFile = File.createTempFile("cache", ".tmp");
        server.response(MocoCache.cache(file(response.getAbsolutePath()), MocoCache.with(file(cacheFile.getAbsolutePath()))));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo"));
                    assertThat(Files.toString(cacheFile, Charset.defaultCharset()), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_response_with_persistence_file() throws IOException {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");
        final File cacheFile = File.createTempFile("cache", ".tmp");
        server.response(MocoCache.cache(file(response.getAbsolutePath()), MocoCache.with(file(cacheFile.getAbsolutePath()))));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        server.response(MocoCache.cache(file(response.getAbsolutePath()), MocoCache.with(file(cacheFile.getAbsolutePath()))));
        response.delete();
        assertThat(response.exists(), is(false));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void changeFileContent(File response, String content) throws FileNotFoundException {
        PrintStream stream = new PrintStream(new FileOutputStream(response));
        stream.print(content);
    }
}
