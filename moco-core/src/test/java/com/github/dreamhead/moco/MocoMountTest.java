package com.github.dreamhead.moco;

import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.MocoMount.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMountTest extends AbstractMocoTest {

    public static final String MOUNT_DIR = "src/test/resources/test";

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_dir_does_not_exist() {
        server.mount("unknown_dir", to("/dir"));
    }

    @Test
    public void should_mount_dir_to_uri() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/dir/dir.response")), is("response from dir"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_bad_request_for_nonexistence_file() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(remoteUrl("/dir/unknown.response"));
            }
        });
    }

    @Test
    public void should_return_inclusion_file() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"), include("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/dir/dir.response")), is("response from dir"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_non_inclusion_file() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"), include("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(remoteUrl("/dir/foo.bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_exclusion_file() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"), exclude("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(remoteUrl("/dir/dir.response"));
            }
        });
    }

    @Test
    public void should_return_non_exclusion_file() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"), exclude("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/dir/foo.bar")), is("foo.bar"));
            }
        });
    }
}
