package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.monitor.FileMocoRunnerMonitor;
import com.github.dreamhead.moco.util.Idles;
import org.junit.After;

import java.io.*;

import static java.lang.String.format;
import static org.junit.Assert.fail;

public abstract class AbstractRunnerTest {
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
            Idles.idle(FileMocoRunnerMonitor.INTERVAL * 2);
        } catch (Exception e) {
            fail(format("failed to wait change happens: %s", e.getMessage()));
        }
    }

    protected void changeFileContent(File response, String content) {
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
