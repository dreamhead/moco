package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJsonTest {
    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        server = httpserver(port());
    }

    @Test
    public void should_match_extact_json() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(json(text(jsonContent))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonContent), is("foo"));
            }
        });
    }

    @Test
    public void should_match_same_structure_json() throws Exception {
        server.request(json(text("{\"foo\":\"bar\"}"))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\n\t\"foo\":\"bar\"\n}"), is("foo"));
            }
        });
    }
}
