package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.bootstrap.parser.HttpArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.HttpsArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.SocketArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.StartArgsParser;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StartArgsTest {

    private StartArgsParser startArgsParser;

    @Before
    public void setUp() throws Exception {
        startArgsParser = new HttpArgsParser();
    }

    @Test
    public void should_parse_start_arguments() {
        StartArgs args = startArgsParser.parse(new String[]{"start", "-p", "12306", "-c", "foo.json"});
        assertThat(args.getPort().get(), is(12306));
        assertThat(args.getConfigurationFile().get(), is("foo.json"));
    }

    @Test
    public void should_parse_settings() {
        StartArgs args = startArgsParser.parse(new String[]{"start", "-p", "12306", "-g", "settings.json"});
        assertThat(args.getSettings().get(), is("settings.json"));
    }

    @Test
    public void should_parse_environment() {
        StartArgs args = startArgsParser.parse(new String[]{"start", "-p", "12306", "-g", "setting.json", "-e", "foo"});
        assertThat(args.getEnv().get(), is("foo"));
    }

    @Test(expected = ParseArgException.class)
    public void should_set_at_least_config_or_settings() {
        startArgsParser.parse(new String[]{"start", "-p", "12306"});
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_config_and_settings() {
        startArgsParser.parse(new String[]{"start", "-p", "12306", "-c", "foo.json", "-g", "settings.json"});
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_environment_without_global_settings() {
        startArgsParser.parse(new String[]{"start", "-p", "12306", "-e", "foo"});
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_environment_with_config() {
        startArgsParser.parse(new String[]{"start", "-p", "12306", "-c", "foo.json", "-e", "foo"});
    }

    @Test
    public void should_parse_without_port() {
        StartArgs args = startArgsParser.parse(new String[]{"start", "-c", "foo.json"});
        assertThat(args.getPort(), is(Optional.<Integer>absent()));
        assertThat(args.getConfigurationFile().get(), is("foo.json"));
    }

    @Test
    public void should_parse_socket() {
        StartArgs args = new SocketArgsParser().parse(new String[]{"start", "-c", "foo.json"});
        assertThat(args.isSocket(), is(true));
    }

    @Test
    public void should_parse_with_default_watch_service() {
        StartArgs args = startArgsParser.parse(new String[]{"start", "-c", "foo.json"});
        assertThat(args.isWatchService(), is(false));
    }

    @Test
    public void should_parse_watch_service() throws Exception {
        StartArgs args = startArgsParser.parse(new String[]{"start", "--watch-service", "-c", "foo.json"});
        assertThat(args.isWatchService(), is(true));
    }

    @Test
    public void should_parse_socket_watch_service() throws Exception {
        StartArgs args = new SocketArgsParser().parse(new String[]{"socket", "--watch-service", "-c", "foo.json"});
        assertThat(args.isWatchService(), is(true));
    }

    @Test
    public void should_parse_https_watch_service() throws Exception {
        StartArgs args = new HttpsArgsParser().parse(new String[]{"https", "--watch-service", "-c", "foo.json", "--https", "/path/to/cert.jks", "--cert", "mocohttps", "--keystore", "mocohttps"});
        assertThat(args.isWatchService(), is(true));
    }
}
