package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.JsonRunner;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.port;

public class AbstractMocoStandaloneTest {
    protected final MocoTestHelper helper = new MocoTestHelper();
    protected JsonRunner runner;

    @Before
    public void setup() throws IOException {
        runner = new JsonRunner();
    }

    @After
    public void teardown() {
        runner.stop();
    }

    protected void runWithConfiguration(String resourceName) {
        try {
            runner.run(Resources.getResource(resourceName).openStream(), port());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
