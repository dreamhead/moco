package com.github.dreamhead.moco;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.dreamhead.moco.helper.MocoTestHelper;

public class MocoRunnerBeforeClassTest {

    private static final String LEVEL_ONE_URL = "/1";
    private static final String LEVEL_TWO_URL = LEVEL_ONE_URL + "/2";
    private static final String LEVEL_ONE_RESPONSE = "levelone";
    private static final String LEVEL_TWO_RESPONSE = "leveltwo";
    private static final String ROOT_RESPONSE = "foo";

    private static Runner runner;
    private static HttpServer server;
    private static MocoTestHelper helper;

    @BeforeClass
    public static void setup() {
        server = httpserver(port());
        runner = Runner.runner(server);
        runner.start();
        server.response(ROOT_RESPONSE);
        server.request(by(uri(LEVEL_ONE_URL))).response(LEVEL_ONE_RESPONSE);
        helper = new MocoTestHelper();
    }

    @AfterClass
    public static void tearDown() {
        runner.stop();
    }

    @Test
    public void should_work_for_root() throws IOException {
        assertThat(helper.get(root()), is(ROOT_RESPONSE));
    }

    @Test
    public void should_work_for_level_one_url() throws IOException {
        assertThat(helper.get(remoteUrl(port(), LEVEL_ONE_URL)),
                is(LEVEL_ONE_RESPONSE));
    }

    @Test
    public void should_work_for_level_two_url_in_test() throws IOException {
        server.request(by(uri(LEVEL_TWO_URL))).response(LEVEL_TWO_RESPONSE);
        assertThat(helper.get(remoteUrl(port(), LEVEL_TWO_URL)),
                is(LEVEL_TWO_RESPONSE));
    }

}
