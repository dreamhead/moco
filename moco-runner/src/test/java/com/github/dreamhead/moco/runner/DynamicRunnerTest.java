package com.github.dreamhead.moco.runner;

import org.junit.Test;

import java.io.*;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DynamicRunnerTest extends AbstractRunnerTest {
    @Test
    public void should_reload_configuration() throws IOException, InterruptedException {
        final File config = File.createTempFile("config", ".json");
        changeFileContent(config, "[{\"response\" :{" +
                "\"text\" : \"foo\"" +
                "}}]");

        runner = new DynamicRunner(config.getAbsolutePath(), port());
        runner.run();
        assertThat(helper.get(root()), is("foo"));

        changeFileContent(config, "[{\"response\" :{" +
                "\"text\" : \"foobar\"" +
                "}}]");

        waitChangeHappens();

        assertThat(helper.get(root()), is("foobar"));
    }

    private void changeFileContent(File response, String content) throws FileNotFoundException {
        PrintStream stream = null;
        try {
            stream = new PrintStream(new FileOutputStream(response));
            stream.print(content);
        } catch (IOException e) {
            fail("failed to change file content");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
