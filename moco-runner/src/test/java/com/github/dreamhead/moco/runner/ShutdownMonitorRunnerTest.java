package com.github.dreamhead.moco.runner;

import org.apache.http.conn.HttpHostConnectException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ShutdownMonitorRunnerTest extends AbstractRunnerTest {
    @Test(expected = HttpHostConnectException.class)
    public void should_shutdown_runner() throws IOException, InterruptedException {
        Runner rawRunner = new DynamicRunner("src/test/resources/foo.json", port());
        File shutdownFile = File.createTempFile("shutdown", "hook");
        shutdownFile.delete();
        runner = new ShutdownMonitorRunner(rawRunner, shutdownFile.getAbsolutePath());
        runner.run();

        assertThat(helper.get(root()), is("foo"));

        shutdownFile.createNewFile();
        waitChangeHappens();

        helper.get(root());
    }
}
