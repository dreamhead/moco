package com.github.dreamhead.moco;

import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMountStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_mount_dir_to_uri() throws IOException {
        runWithConfiguration("mount.json");
        assertThat(helper.get(remoteUrl("/mount/mount.response")), is("response from mount"));
    }

    @Test
    public void should_mount_dir_to_uri_with_include() throws IOException {
        runWithConfiguration("mount.json");
        assertThat(helper.get(remoteUrl("/mount-include/mount.response")), is("response from mount"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_non_inclusion() throws IOException {
        runWithConfiguration("mount.json");
        helper.get(remoteUrl("/mount-include/foo.bar"));
    }

    @Test
    public void should_mount_dir_to_uri_with_exclude() throws IOException {
        runWithConfiguration("mount.json");
        assertThat(helper.get(remoteUrl("/mount-exclude/foo.bar")), is("foo.bar"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_exclusion() throws IOException {
        runWithConfiguration("mount.json");
        helper.get(remoteUrl("/mount-exclude/mount.response"));
    }
}
