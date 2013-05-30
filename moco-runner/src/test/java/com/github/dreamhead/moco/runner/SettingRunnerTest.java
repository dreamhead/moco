package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SettingRunnerTest {
    private final MocoTestHelper helper = new MocoTestHelper();
    private SettingRunner runner;

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void should_run_with_setting() throws IOException {
        InputStream stream = SettingRunnerTest.class.getClassLoader().getResourceAsStream("multiple/settings.json");
        runner = new SettingRunner(stream, 12306);
        runner.run();

        assertThat(helper.get(remoteUrl("/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar")), is("bar"));
    }
}
