package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
}
