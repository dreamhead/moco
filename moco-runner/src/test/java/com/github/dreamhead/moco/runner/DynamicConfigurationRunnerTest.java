package com.github.dreamhead.moco.runner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DynamicConfigurationRunnerTest extends AbstractRunnerTest {

    @Test
    public void should_reload_configuration(@TempDir final Path path) throws IOException, InterruptedException {
        final File config = path.resolve("tempfile").toFile();
        changeFileContent(config, "[{\"response\" :{"
                + "\"text\" : \"foo\""
                + "}}]");

        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs()
                .withPort(port())
                .withShutdownPort(9090)
                .withConfigurationFile(config.getAbsolutePath())
                .build());
        runner.run();
        assertThat(helper.get(root()), is("foo"));

        changeFileContent(config, "[{\"response\" :{"
                + "\"text\" : \"foobar\""
                + "}}]");

        waitChangeHappens();

        assertThat(helper.get(root()), is("foobar"));
        runner.stop();
    }
}
