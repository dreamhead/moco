package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.JsonRunner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.After;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.runner.JsonRunner.newJsonRunnerWithStreams;
import static com.google.common.collect.ImmutableList.toImmutableList;

public class AbstractMocoStandaloneTest {
    protected final MocoTestHelper helper = new MocoTestHelper();
    protected JsonRunner runner;

    @After
    public void teardown() {
        if (runner != null) {
            runner.stop();
        }
    }

    protected void runWithConfiguration(final String... resourceNames) {
        runner = newRunner(resourceNames);
        runner.run();
    }

    private JsonRunner newRunner(final String[] resourceNames) {
        final ImmutableList<InputStream> streams = Arrays.stream(resourceNames)
                .map(this::resourceAsStream)
                .collect(toImmutableList());
        return newJsonRunnerWithStreams(streams, httpArgs().withPort(port()).build());
    }

    private InputStream resourceAsStream(String resourceName) {
        try {
            return Resources.getResource(resourceName).openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
