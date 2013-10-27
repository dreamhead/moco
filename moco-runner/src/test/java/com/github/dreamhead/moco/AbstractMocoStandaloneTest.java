package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.JsonRunner;
import com.google.common.io.Resources;
import org.junit.After;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.runner.JsonRunner.newJsonRunnerWithStreams;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;

public class AbstractMocoStandaloneTest {
    protected final MocoTestHelper helper = new MocoTestHelper();
    private JsonRunner runner;

    @After
    public void teardown() {
        if (runner != null) {
            runner.stop();
        }
    }

    protected void runWithConfiguration(String... resourceNames) {
        try {
            runner = newRunner(resourceNames);
            runner.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonRunner newRunner(String[] resourceNames) throws IOException {
        List<InputStream> streams = newArrayList();
        for (String resourceName : resourceNames) {
            streams.add(Resources.getResource(resourceName).openStream());
        }
        return newJsonRunnerWithStreams(streams, of(port()));
    }
}
