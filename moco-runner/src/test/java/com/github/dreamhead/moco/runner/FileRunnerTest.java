package com.github.dreamhead.moco.runner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileRunnerTest extends AbstractRunnerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void should_load_configurations() throws Exception {
        final File config1 = tempFolder.newFile();
        changeFileContent(config1, "[{\"response\" :{"
                + "\"text\" : \"foo\""
                + "}}]");

        final File config2 = tempFolder.newFile();
        changeFileContent(config2, "[{" +
                "\"request\": {\"uri\": \"/bar\"}," +
                "\"response\": {\"text\": \"bar\"}" +
                "}]");

        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs()
                .withPort(port())
                .withShutdownPort(9090)
                .withConfigurationFile(config1.getAbsolutePath(), config2.getAbsolutePath())
                .build());
        runner.run();

        assertThat(helper.get(root()), is("foo"));
        assertThat(helper.get(remoteUrl("/bar")), is("bar"));
    }
}