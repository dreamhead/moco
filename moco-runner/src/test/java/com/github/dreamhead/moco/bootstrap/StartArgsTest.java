package com.github.dreamhead.moco.bootstrap;

import com.google.common.base.Optional;
import org.junit.Test;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StartArgsTest {
    @Test
    public void should_parse_start_arguments() {
        StartArgs args = parse(ServerType.HTTP, "start", "-p", "12306", "-c", "foo.json");
        assertThat(args.getPort().get(), is(12306));
        assertThat(args.getConfigurationFile().get(), is("foo.json"));
    }

    @Test
    public void should_parse_settings() {
        StartArgs args = parse(ServerType.HTTP, "start", "-p", "12306", "-g", "settings.json");
        assertThat(args.getSettings().get(), is("settings.json"));
    }

    @Test
    public void should_parse_environment() {
        StartArgs args = parse(ServerType.HTTP, "start", "-p", "12306", "-g", "setting.json", "-e", "foo");
        assertThat(args.getEnv().get(), is("foo"));
    }

    @Test(expected = ParseArgException.class)
    public void should_set_at_least_config_or_settings() {
        parse(ServerType.HTTP, "start", "-p", "12306");
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_config_and_settings() {
        parse(ServerType.HTTP, "start", "-p", "12306", "-c", "foo.json", "-g", "settings.json");
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_environment_without_global_settings() {
        parse(ServerType.HTTP, "start", "-p", "12306", "-e", "foo");
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_environment_with_config() {
        parse(ServerType.HTTP, "start", "-p", "12306", "-c", "foo.json", "-e", "foo");
    }

    @Test
    public void should_parse_without_port() {
        StartArgs args = parse(ServerType.HTTP, "start", "-c", "foo.json");
        assertThat(args.getPort(), is(Optional.<Integer>absent()));
        assertThat(args.getConfigurationFile().get(), is("foo.json"));
    }

    @Test
    public void should_parse_socket() {
        StartArgs args = parse(ServerType.SOCKET, "start", "-c", "foo.json");
        assertThat(args.isSocket(), is(true));
    }
}
