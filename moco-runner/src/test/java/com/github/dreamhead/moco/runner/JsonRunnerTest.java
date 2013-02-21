package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonRunnerTest {
    protected final MocoTestHelper helper = new MocoTestHelper();
    private JsonRunner runner;

    @Before
    public void setUp() throws Exception {
        runner = new JsonRunner();
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void should_reload_configuration() throws IOException, InterruptedException {
        final File config = File.createTempFile("config", ".json");
        changeFileContent(config, "[{\"response\" :{" +
                "\"text\" : \"foo\"" +
                "}}]");

        runner.run(config.getAbsolutePath(), port());
        assertThat(helper.get(root()), is("foo"));

        changeFileContent(config, "[{\"response\" :{" +
                "\"text\" : \"bar\"" +
                "}}]");

        Thread.sleep(1500);

        assertThat(helper.get(root()), is("bar"));
    }

    private void changeFileContent(File response, String content) throws FileNotFoundException {
        PrintStream stream = null;
        try {
            stream = new PrintStream(new FileOutputStream(response));
            stream.print(content);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
