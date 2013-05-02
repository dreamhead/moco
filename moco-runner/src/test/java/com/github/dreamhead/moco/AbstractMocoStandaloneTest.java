package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.JsonRunner;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.google.common.collect.Lists.newArrayList;

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

    protected void runWithConfiguration(String... resourceNames) {
        try {
            List<InputStream> streams = newArrayList();
            for (String resourceName : resourceNames) {
                streams.add(Resources.getResource(resourceName).openStream());
            }
            runner.run(streams, port());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
