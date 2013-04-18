package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoPathResourceTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_path_resource_based_on_specified_request() throws IOException {
        runWithConfiguration("path_resource.json");
        assertThat(helper.get(remoteUrl("/path-resource")), is("response from path"));
    }
}
