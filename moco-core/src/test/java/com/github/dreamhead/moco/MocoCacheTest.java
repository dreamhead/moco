package com.github.dreamhead.moco;

import com.google.common.io.Files;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.MocoCache.cache;
import static com.github.dreamhead.moco.MocoCache.with;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoCacheTest extends AbstractMocoTest {
    @Test
    public void should_change_file_content_dynamically() throws Exception {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");

        server.response(file(response.getAbsolutePath()));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo"));
                changeFileContent(response, "bar");
                assertThat(helper.get(root()), is("bar"));
            }
        });

    }

    @Test
    public void should_cache_result() throws Exception {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");

        server.response(cache(file(response.getAbsolutePath())));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo"));
                changeFileContent(response, "bar");
                assertThat(helper.get(root()), is("foo"));
            }
        });
    }

    @Test
    public void should_cache_with_persistence_file() throws Exception {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");
        final File cacheFile = File.createTempFile("cache", ".tmp");
        server.response(cache(file(response.getAbsolutePath()), with(file(cacheFile.getAbsolutePath()))));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo"));
                assertThat(Files.toString(cacheFile, Charset.defaultCharset()), is("foo"));
            }
        });
    }

    @Test
    public void should_response_with_persistence_file() throws Exception {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");
        final File cacheFile = File.createTempFile("cache", ".tmp");
        server.response(cache(file(response.getAbsolutePath()), with(file(cacheFile.getAbsolutePath()))));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo"));
            }
        });

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

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_resource_and_persistence_file() throws Exception {
        final File response = File.createTempFile("response", ".tmp");
        changeFileContent(response, "foo");
        final File cacheFile = File.createTempFile("cache", ".tmp");
        server.response(cache(file(response.getAbsolutePath()), with(file(cacheFile.getAbsolutePath()))));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo"));
            }
        });

        HttpServer server = httpserver(12306);
        server.response(cache(file(response.getAbsolutePath()), with(file(cacheFile.getAbsolutePath()))));
        response.delete();
        assertThat(response.exists(), is(false));
        cacheFile.delete();
        assertThat(cacheFile.exists(), is(false));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(root());
            }
        });
    }

    private void changeFileContent(File response, String content) throws FileNotFoundException {
        PrintStream stream = new PrintStream(new FileOutputStream(response));
        stream.print(content);
        stream.close();
    }
}
