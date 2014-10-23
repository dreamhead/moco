package com.github.dreamhead.moco.runner;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import com.github.dreamhead.moco.bootstrap.ServerType;
import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.junit.After;
import org.junit.Test;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.helper.MocoTestHelper;

public class SettingRunnerTest {
    private final MocoTestHelper helper = new MocoTestHelper();
    private SettingRunner runner;

    @After
    public void tearDown() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void should_run_with_setting() throws IOException {
        InputStream stream = getResourceAsStream("settings/settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar")), is("bar"));
    }

    @Test
    public void should_run_with_setting_with_context() throws IOException {
        InputStream stream = getResourceAsStream("settings/context-settingss.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar/bar")), is("bar"));
    }

    @Test
    public void should_run_with_setting_with_file_root() throws IOException {
        InputStream stream = getResourceAsStream("settings/fileroot-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/fileroot/fileroot")), is("foo.response"));
    }

    @Test
    public void should_run_with_env() throws IOException {
        InputStream stream = getResourceAsStream("settings/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "foo"));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo/foo")), is("foo"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_run_without_env() throws IOException {
        InputStream stream = getResourceAsStream("settings/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "bar"));
        runner.run();

        helper.get(remoteUrl("/foo/foo"));
    }

    @Test
    public void should_run_with_global_response_settings() throws IOException {
        InputStream stream = getResourceAsStream("settings/response-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        Header header = Request.Get(remoteUrl("/foo")).execute().returnResponse().getFirstHeader("foo");
        assertThat(header.getValue(), is("bar"));
    }

    private StartArgs createStartArgs(int port, String env) {
        return StartArgs.builder().withType(ServerType.HTTP).withPort(port).withEnv(env).build();
    }

    private StartArgs createStartArgs(int port) {
        return StartArgs.builder().withType(ServerType.HTTP).withPort(port).build();
    }
    
    private InputStream getResourceAsStream(String filename) {
        return SettingRunnerTest.class.getClassLoader().getResourceAsStream(filename);
    }
}
