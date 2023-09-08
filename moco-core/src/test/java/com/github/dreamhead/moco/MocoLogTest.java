package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.internal.SessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.io.Files.asCharSource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class MocoLogTest {
    private MocoTestHelper helper;

    @BeforeEach
    public void setUp() {
        this.helper = new MocoTestHelper();
    }

    @Test
    public void should_log_request_and_response(@TempDir final Path path) throws Exception {
        HttpServer server = httpServer(port(), log());
        server.request(by("0XCAFE")).response("0XBABE");

        File file = path.resolve("tempfile").toFile();

        System.setOut(new PrintStream(new FileOutputStream(file)));

        running(server, () -> assertThat(helper.postContent(root(), "0XCAFE"), is("0XBABE")));

        String actual = asCharSource(file, Charset.defaultCharset()).read();
        assertThat(actual, containsString("0XBABE"));
        assertThat(actual, containsString("0XCAFE"));
    }

    @Test
    public void should_log_request_and_response_into_file(@TempDir final Path path) throws Exception {
        File file = path.resolve("tempfile").toFile();
        HttpServer server = httpServer(port(), log(file.getAbsolutePath()));
        server.request(by("0XCAFE")).response("0XBABE");

        running(server, () -> assertThat(helper.postContent(root(), "0XCAFE"), is("0XBABE")));

        String actual = asCharSource(file, Charset.defaultCharset()).read();
        assertThat(actual, containsString("0XBABE"));
        assertThat(actual, containsString("0XCAFE"));
    }

    @Test
    public void should_log_request_and_response_with_exception(@TempDir final Path path) throws Exception {
        File file = path.resolve("tempfile").toFile();

        HttpServer server = httpServer(port(), log(file.getAbsolutePath()));
        ResponseHandler mock = mock(ResponseHandler.class);
        doThrow(RuntimeException.class).when(mock).writeToResponse(any(SessionContext.class));

        server.request(by("0XCAFE")).response(mock);

        running(server, () -> {
            try {
                helper.postContent(root(), "0XCAFE");
            } catch (IOException ignored) {
            }
        });

        String actual = asCharSource(file, Charset.defaultCharset()).read();
        assertThat(actual, containsString("RuntimeException"));
    }

    @Test
    public void should_log_request_and_response_into_file_with_charset(@TempDir final Path path) throws Exception {
        File file = path.resolve("tempfile").toFile();
        HttpServer server = httpServer(port(), log(file.getAbsolutePath(), StandardCharsets.UTF_8));
        server.request(by("0XCAFE")).response("0XBABE");

        running(server, () -> assertThat(helper.postContent(root(), "0XCAFE"), is("0XBABE")));

        String actual = asCharSource(file, Charset.defaultCharset()).read();
        assertThat(actual, containsString("0XBABE"));
        assertThat(actual, containsString("0XCAFE"));
    }

    @Test
    public void should_log_request_and_response_with_queries(@TempDir final Path path) throws Exception {
        HttpServer server = httpServer(port(), log());
        server.request(by("0XCAFE")).response("0XBABE");
        File file = path.resolve("tempfile").toFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));

        running(server, () -> assertThat(helper.postContent(remoteUrl("/foo?param=actual"), "0XCAFE"), is("0XBABE")));

        String actual = asCharSource(file, Charset.defaultCharset()).read();
        assertThat(actual, containsString("0XBABE"));
        assertThat(actual, containsString("0XCAFE"));
        assertThat(actual, containsString("/foo?param=actual"));
    }
}
