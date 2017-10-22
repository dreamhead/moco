package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.util.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DynamicSettingRunnerTest extends AbstractRunnerTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void should_load_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile("config.json");
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
    public void should_load_glob_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile("config.json");
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
    public void should_reload_configuration() throws IOException, InterruptedException {
        final File config = tempFolder.newFile("config.json");
        changeFileContent(config, "[{\"response\" :{"
                + "\"text\" : \"foo\""
                + "}}]");

        final File setting = tempFolder.newFile("settings.json");
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
    public void should_reload_configuration_with_multiple_modification() throws IOException, InterruptedException {
        final File config1 = tempFolder.newFile("config1.json");
        changeFileContent(config1, "[{" +
                "        \"request\": {" +
                "            \"uri\": \"/foo\"" +
                "        }," +
                "        \"response\": {" +
                "            \"text\": \"foo\"" +
                "        }" +
                "}]");

        final File config2 = tempFolder.newFile("config2.json");
        changeFileContent(config2, "[{" +
                "        \"request\": {" +
                "            \"uri\": \"/bar\"" +
                "        }," +
                "        \"response\": {" +
                "            \"text\": \"bar\"" +
                "        }" +
                "}]");


        final File setting = tempFolder.newFile("settings.json");
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
