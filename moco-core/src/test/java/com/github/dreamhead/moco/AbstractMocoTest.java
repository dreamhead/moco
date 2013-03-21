package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.RemoteTestUtils.port;

public class AbstractMocoTest {
    protected HttpServer server;
    protected MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        server = httpserver(port());
    }
}
