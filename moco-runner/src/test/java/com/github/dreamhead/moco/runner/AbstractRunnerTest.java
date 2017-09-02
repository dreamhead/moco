package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.After;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.util.Idles.idle;
import static java.lang.String.format;
import static org.junit.Assert.fail;

public abstract class AbstractRunnerTest {
    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(1);

    protected final MocoTestHelper helper = new MocoTestHelper();
    protected Runner runner;

    @After
    public void tearDown() {
        if (runner != null) {
            runner.stop();
        }
    }

    protected void waitChangeHappens() {
        try {
            idle(INTERVAL * 3, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            fail(format("failed to wait change happens: %s", e.getMessage()));
        }
    }

    protected void changeFileContent(final File response, final String content) {
        try (PrintStream stream = new PrintStream(new FileOutputStream(response))){
            stream.print(content);
        } catch (IOException e) {
            fail("failed to change file content");
        }
    }
}
