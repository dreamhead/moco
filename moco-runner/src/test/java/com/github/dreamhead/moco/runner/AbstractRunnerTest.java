package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.After;

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
            Thread.sleep(FileMonitor.INTERVAL * 2);
        } catch (InterruptedException e) {
            fail(format("failed to wait change happens: %s", e.getMessage()));
        }
    }
}
