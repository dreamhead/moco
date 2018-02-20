package com.github.dreamhead.moco;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
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

        URL resource = Resources.getResource("path.request");
        InputStream stream = resource.openStream();
        assertThat(helper.postStream(root(), stream), is("path resource"));
    }
}
