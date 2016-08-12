package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRedirectStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_redirect_to_expected_url() throws IOException {
        runWithConfiguration("redirect.json");

        assertThat(helper.get(remoteUrl("/redirect")), is("foo"));
    }

    @Test
    public void should_redirect_to_expected_url_with_template() throws IOException {
        runWithConfiguration("redirect.json");

        assertThat(helper.get(remoteUrl("/redirect-with-template")), is("foo"));
    }

    @Test
    public void should_redirect_to_expected_url_with_path_resource() throws IOException {
        runWithConfiguration("redirect.json");

        assertThat(helper.get(remoteUrl("/redirect-with-path-resource")), is("foo"));
    }

}
