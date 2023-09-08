package com.github.dreamhead.moco.bootstrap;

import org.junit.jupiter.api.Test;

import static com.github.dreamhead.moco.bootstrap.ShutdownArgs.parse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShutdownArgsTest {
    @Test
    public void should_parse_shutdown_arguments() {
        ShutdownArgs args = parse(new String[]{"shutdown", "-s", "12305"});
        assertThat(12305, is(args.getShutdownPort().get()));
    }

    @Test
    public void should_parse_shutdown_default_arguments() {
        ShutdownArgs args = parse(new String[]{"shutdown"});
        assertThat(args.getShutdownPort().isPresent(), is(false));
    }

    @Test
    public void should_set_shutdown_port() {
        assertThrows(ParseArgException.class, () -> {
            parse(new String[]{"shutdown", "-s"});
        });
    }
}
