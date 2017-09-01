package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMulipleConfigurationsTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_run_with_many_configurations() throws IOException {
        runWithConfiguration("settings/details/foo.json", "settings/details/bar.json");
        assertThat(helper.get(remoteUrl("/foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/bar")), is("bar"));
    }
}
