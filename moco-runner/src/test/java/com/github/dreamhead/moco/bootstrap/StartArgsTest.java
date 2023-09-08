package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.bootstrap.parser.HttpArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.SocketArgsParser;
import com.github.dreamhead.moco.bootstrap.parser.StartArgsParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StartArgsTest {

    private StartArgsParser startArgsParser;

    @BeforeEach
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

    @Test
    public void should_set_at_least_config_or_settings() {
        assertThrows(ParseArgException.class, () -> {
            startArgsParser.parse(new String[]{"start", "-p", "12306"});
        });
    }

    @Test
    public void should_not_set_config_and_settings() {
        assertThrows(ParseArgException.class, () -> {
            startArgsParser.parse(new String[]{"start", "-p", "12306", "-c", "foo.json", "-g", "settings.json"});
        });
    }

    @Test
    public void should_not_set_environment_without_global_settings() {
        assertThrows(ParseArgException.class, () -> {
            startArgsParser.parse(new String[]{"start", "-p", "12306", "-e", "foo"});
        });
    }

    @Test
    public void should_not_set_environment_with_config() {
        assertThrows(ParseArgException.class, () -> {
            startArgsParser.parse(new String[]{"start", "-p", "12306", "-c", "foo.json", "-e", "foo"});
        });
    }

    @Test
    public void should_parse_without_port() {
        StartArgs args = startArgsParser.parse(new String[]{"start", "-c", "foo.json"});
        assertThat(args.getPort().isPresent(), is(false));
        assertThat(args.getConfigurationFile().get(), is("foo.json"));
    }

    @Test
    public void should_parse_socket() {
        StartArgs args = new SocketArgsParser().parse(new String[]{"start", "-c", "foo.json"});
        assertThat(args.isSocket(), is(true));
    }
}
