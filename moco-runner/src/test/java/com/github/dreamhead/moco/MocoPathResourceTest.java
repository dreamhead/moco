package com.github.dreamhead.moco;

import com.google.common.io.ByteStreams;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoPathResourceTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_path_resource_based_on_specified_request() throws IOException {
        runWithConfiguration("path_resource.json");
        assertThat(helper.get(remoteUrl("/path-resource")), is("response from path"));
    }

    @Test
    public void should_return_response_based_on_path_resource() throws IOException {
        runWithConfiguration("path_resource.json");

        InputStream asStream = this.getClass().getClassLoader().getResourceAsStream("path.request");
        assertThat(helper.postBytes(root(), ByteStreams.toByteArray(asStream)), is("path resource"));
    }
}
