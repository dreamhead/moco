package com.github.dreamhead.moco.bootstrap.parser;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class StartArgsParserTest {
    @Test
    public void http_args_parse_should_get_configuration_files() throws Exception {
        final StartArgs args = new HttpArgsParser().parse(new String[]{"http", "-c", "a", "b"});

        assertThat(args.getConfigurationFiles().isPresent(), is(true));
        assertThat(args.getConfigurationFiles().get(), is(new String[]{"a", "b"}));
    }

    @Test
    public void https_args_parse_should_get_configuration_files() throws Exception {
        final StartArgs args = new HttpsArgsParser().parse(new String[]{"https", "-g", "a", "--https", "/path/to/cert.jks", "--cert", "mocohttps", "--keystore", "mocohttps"});

        assertThat(args.getConfigurationFiles().isPresent(), is(false));
    }

    @Test
    public void socket_args_parse_should_get_configuration_files() throws Exception {
        final StartArgs args = new SocketArgsParser().parse(new String[]{"socket", "-c", "a", "b"});

        assertThat(args.getConfigurationFiles().isPresent(), is(true));
        assertThat(args.getConfigurationFiles().get(), is(new String[]{"a", "b"}));
    }
}