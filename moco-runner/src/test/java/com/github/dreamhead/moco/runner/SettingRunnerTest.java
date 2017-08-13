package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SettingRunnerTest {
    private final MocoTestHelper helper = new MocoTestHelper();
    private SettingRunner runner;
    private InputStream stream;

    @After
    public void tearDown() throws IOException {
        if (runner != null) {
            runner.stop();
        }

        if (stream != null) {
            stream.close();
            stream = null;
        }
    }

    @Test
    public void should_run_with_setting() throws IOException {
        stream = getResourceAsStream("settings/settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar")), is("bar"));
    }

    @Test
    public void should_run_with_setting_with_context() throws IOException {
        stream = getResourceAsStream("settings/context-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar/bar")), is("bar"));
    }

    @Test
    public void should_run_with_setting_with_file_root() throws IOException {
        stream = getResourceAsStream("settings/fileroot-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.get(remoteUrl("/fileroot/fileroot")), is("foo.response"));
    }

    @Test
    public void should_run_with_env() throws IOException {
        stream = getResourceAsStream("settings/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "foo"));
        runner.run();

        assertThat(helper.get(remoteUrl("/foo/foo")), is("foo"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_run_without_env() throws IOException {
        stream = getResourceAsStream("settings/env-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306, "bar"));
        runner.run();

        helper.get(remoteUrl("/foo/foo"));
    }

    @Test
    public void should_run_with_global_response_settings() throws IOException {
        stream = getResourceAsStream("settings/response-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        Header header = helper.getResponse(remoteUrl("/foo")).getFirstHeader("foo");
        assertThat(header.getValue(), is("bar"));
    }

    @Test
    public void should_run_with_global_request_settings() throws IOException {
        stream = getResourceAsStream("settings/request-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        assertThat(helper.getWithHeader(remoteUrl("/foo"), of("foo", "bar")), is("foo"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_global_request_settings() throws IOException {
        stream = getResourceAsStream("settings/request-settings.json");
        runner = new SettingRunner(stream, createStartArgs(12306));
        runner.run();

        helper.get(remoteUrl("/foo"));
    }

    private StartArgs createStartArgs(final int port, final String env) {
        return httpArgs().withPort(port).withEnv(env).build();
    }

    private StartArgs createStartArgs(final int port) {
        return httpArgs().withPort(port).build();
    }

    private InputStream getResourceAsStream(final String filename) {
        return SettingRunnerTest.class.getClassLoader().getResourceAsStream(filename);
    }
}
