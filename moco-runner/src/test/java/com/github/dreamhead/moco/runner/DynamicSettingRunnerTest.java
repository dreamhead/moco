package com.github.dreamhead.moco.runner;

import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DynamicSettingRunnerTest extends AbstractRunnerTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void should_reload_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile("config.json");
        changeFileContent(config, "[{\"response\" :{"
                + "\"text\" : \"foo\""
                + "}}]");

        final File setting = tempFolder.newFile("settings.json");
        String path = FilenameUtils.separatorsToUnix(config.getAbsolutePath());
        changeFileContent(setting, "[{"
                + "\"include\" : \"" + path + "\""
                + "}]");

        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs()
                .withPort(port())
                .withShutdownPort(9090)
                .withSettings(setting.getAbsolutePath())
                .build());
        runner.run();
        assertThat(helper.get(root()), is("foo"));

        changeFileContent(config, "[{\"response\" :{"
                + "\"text\" : \"foobar\""
                + "}}]");

        waitChangeHappens();

        assertThat(helper.get(root()), is("foobar"));
    }
}
