package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class MocoLogTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        this.helper = new MocoTestHelper();
    }

    @Test
    public void should_log_request_and_response() throws Exception {
        HttpServer server = httpserver(port(), log());
        server.request(by("0XCAFE")).response("0XBABE");
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(root(), "0XCAFE"), is("0XBABE"));
            }
        });

        String actual = Files.toString(file, Charset.defaultCharset());
        assertThat(actual, containsString("0XCAFE"));
        assertThat(actual, containsString("0XCAFE"));
    }

    @Test
    public void should_log_request_and_response_into_file() throws Exception {
        File file = folder.newFile();
        HttpServer server = httpserver(port(), log(file.getAbsolutePath()));
        server.request(by("0XCAFE")).response("0XBABE");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(root(), "0XCAFE"), is("0XBABE"));
            }
        });

        String actual = Files.toString(file, Charset.defaultCharset());
        assertThat(actual, containsString("0XCAFE"));
        assertThat(actual, containsString("0XCAFE"));
    }

    @Test
    public void should_log_request_and_response_with_exception() throws Exception {
        File file = folder.newFile();
        HttpServer server = httpserver(port(), log(file.getAbsolutePath()));
        ResponseHandler mock = mock(ResponseHandler.class);
        doThrow(RuntimeException.class).when(mock).writeToResponse(any(SessionContext.class));

        server.request(by("0XCAFE")).response(mock);

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.postContent(root(), "0XCAFE");
                } catch (IOException ignored) {
                }
            }
        });

        String actual = Files.toString(file, Charset.defaultCharset());
        assertThat(actual, containsString("RuntimeException"));
    }
}
