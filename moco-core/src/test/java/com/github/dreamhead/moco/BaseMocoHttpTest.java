package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;

public abstract class BaseMocoHttpTest<T extends HttpServer> {
    protected T server;
    protected MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        server = createServer(port());
    }

    protected abstract T createServer(int port);
}
