package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.util.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DynamicSettingRunnerTest extends AbstractRunnerTest {

    @Test
    public void should_load_configuration(@TempDir final Path path) throws IOException, InterruptedException {
        final File config = path.resolve("config.json").toFile();
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
    }

    @Test
    public void should_load_glob_configuration(@TempDir final Path path) throws IOException, InterruptedException {
        final File config = path.resolve("config.json").toFile();
        changeFileContent(config, "[{\"response\" :{"
                + "\"text\" : \"foo\""
                + "}}]");

        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        String absolutePath = config.getParent();
        String result = Files.join(absolutePath, "*.json");
        runner = factory.createRunner(httpArgs()
                .withPort(port())
                .withShutdownPort(9090)
                .withConfigurationFile(result)
                .build());
        runner.run();
        assertThat(helper.get(root()), is("foo"));
    }

    @Test
    public void should_reload_configuration(@TempDir final Path tempFolder) throws IOException, InterruptedException {
        final File config = tempFolder.resolve("config.json").toFile();
        changeFileContent(config, "[{\"response\" :{"
                + "\"text\" : \"foo\""
                + "}}]");

        final File setting = tempFolder.resolve("settings.json").toFile();
        String path = config.getAbsolutePath();
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

    @Test
    public void should_reload_configuration_with_multiple_modification(@TempDir final Path tempFolder) throws IOException, InterruptedException {
        final File config1 = tempFolder.resolve("config1.json").toFile();
        changeFileContent(config1, "[{" +
                "        \"request\": {" +
                "            \"uri\": \"/foo\"" +
                "        }," +
                "        \"response\": {" +
                "            \"text\": \"foo\"" +
                "        }" +
                "}]");

        final File config2 = tempFolder.resolve("config2.json").toFile();
        changeFileContent(config2, "[{" +
                "        \"request\": {" +
                "            \"uri\": \"/bar\"" +
                "        }," +
                "        \"response\": {" +
                "            \"text\": \"bar\"" +
                "        }" +
                "}]");


        final File setting = tempFolder.resolve("settings.json").toFile();
        changeFileContent(setting, "["
                + "{\"include\" : \"" + config1.getAbsolutePath() + "\"},"
                + "{\"include\" : \"" + config2.getAbsolutePath() + "\"}"
                + "]");

        RunnerFactory factory = new RunnerFactory("SHUTDOWN");
        runner = factory.createRunner(httpArgs()
                .withPort(port())
                .withShutdownPort(9090)
                .withSettings(setting.getAbsolutePath())
                .build());
        runner.run();

        assertThat(helper.get(remoteUrl("/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar")), is("bar"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);

        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;

        System.setErr(printStream);
        System.setOut(printStream);

        changeFileContent(config1, "[{" +
                "        \"request\": {" +
                "            \"uri\": \"/foo\"" +
                "        }," +
                "        \"response\": {" +
                "            \"text\": \"foo1\"" +
                "        }" +
                "}]");
        changeFileContent(config2, "[{" +
                "        \"request\": {" +
                "            \"uri\": \"/bar\"" +
                "        }," +
                "        \"response\": {" +
                "            \"text\": \"bar1\"" +
                "        }" +
                "}]");


        waitChangeHappens();

        System.setOut(oldOut);
        System.setErr(oldErr);

        assertThat(helper.get(remoteUrl("/foo")), is("foo1"));

        String result = new String(out.toByteArray());
        assertThat(result.contains("Fail"), is(false));
    }
}
