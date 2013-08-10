package com.github.dreamhead.moco.bootstrap;

import static com.github.dreamhead.moco.bootstrap.ShutdownArgs.parse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class ShutdownArgsTest {

	private final int DEFAULT_SHUTDOWN_PORT = 9527;

	@Test
	public void should_parse_shutdown_arguments() {
		ShutdownArgs args = parse(new String[] { "shutdown", "-s", "12305" });
		assertThat(12305, is(args.getShutdownPort(DEFAULT_SHUTDOWN_PORT)));
	}

	@Test
	public void should_parse_shutdown_default_arguments() {
		ShutdownArgs args = parse(new String[] { "shutdown" });
		assertThat(DEFAULT_SHUTDOWN_PORT,
				is(args.getShutdownPort(DEFAULT_SHUTDOWN_PORT)));
	}

	@Test(expected = ParseArgException.class)
	public void should_set_shutdown_port() {
		parse(new String[] { "shutdown", "-s" });
	}
}
