package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.apache.http.client.HttpResponseException;
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
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar")), is("bar"));
    }

    @Test
    public void should_run_with_setting_with_context() throws IOException {
        InputStream stream = SettingRunnerTest.class.getClassLoader().getResourceAsStream("multiple/context-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar/bar")), is("bar"));
    }

    @Test
    public void should_run_with_setting_with_file_root() throws IOException {
        InputStream stream = SettingRunnerTest.class.getClassLoader().getResourceAsStream("multiple/fileroot-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/fileroot/fileroot")), is("foo.response"));
    }

    @Test
    public void should_run_with_env() throws IOException {
        InputStream stream = SettingRunnerTest.class.getClassLoader().getResourceAsStream("multiple/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "foo"));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo/foo")), is("foo"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_run_without_env() throws IOException {
        InputStream stream = SettingRunnerTest.class.getClassLoader().getResourceAsStream("multiple/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "bar"));
        runner.run();

        helper.get(remoteUrl("/foo/foo"));
    }

    private StartArgs createStartArgs(int port, String env) {
        return new StartArgs(port, null, null, null, env);
    }

    private StartArgs createStartArgs(int port) {
        return new StartArgs(port, null, null, null, null);
    }
}
