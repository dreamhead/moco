package com.github.dreamhead.moco.bootstrap;

import org.junit.Test;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StartArgsTest {
    @Test
    public void should_parse_start_arguments() {
        StartArgs args = parse("start", "-p", "12306", "-c", "foo.json");
        assertThat(args.getPort(), is(12306));
        assertThat(args.getConfigurationFile(), is("foo.json"));
    }

    @Test
    public void should_parse_settings() {
        StartArgs args = parse("start", "-p", "12306", "-g", "settings.json");
        assertThat(args.getSettings(), is("settings.json"));
    }

    @Test(expected = ParseArgException.class)
    public void should_set_at_least_config_or_settings() {
        parse("start", "-p", "12306");
    }

    @Test(expected = ParseArgException.class)
    public void should_not_set_config_and_settings() {
        parse("start", "-p", "12306", "-c", "foo.json", "-g", "settings.json");
    }
}
