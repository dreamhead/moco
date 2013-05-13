package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DynamicSettingRunnerTest extends AbstractRunnerTest {
    @Test
    public void should_reload_configuration() throws IOException, InterruptedException {
        final File config = File.createTempFile("config", ".json");
        changeFileContent(config, "[{\"response\" :{" +
                "\"text\" : \"foo\"" +
                "}}]");

        final File setting = File.createTempFile("setting", ".json");
        changeFileContent(setting, "[{" +
                "\"include\" : \"" + config.getAbsolutePath() + "\"" +
                "}]");

        RunnerFactory factory = new RunnerFactory(9090, "SHUTDOWN");
        runner = factory.createRunner(new StartArgs(port(), 9090, null, setting.getAbsolutePath()));
        runner.run();
        assertThat(helper.get(root()), is("foo"));

        changeFileContent(config, "[{\"response\" :{" +
                "\"text\" : \"foobar\"" +
                "}}]");

        waitChangeHappens();

        assertThat(helper.get(root()), is("foobar"));
    }
}
