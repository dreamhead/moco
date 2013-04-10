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
    }
}
