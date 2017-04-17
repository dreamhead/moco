package com.github.dreamhead.moco;

import com.google.common.io.CharStreams;
import com.google.common.net.HttpHeaders;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.MocoMount.exclude;
import static com.github.dreamhead.moco.MocoMount.include;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMountTest extends AbstractMocoHttpTest {

    private static final String MOUNT_DIR = "src/test/resources/test";

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

    @Test
    public void should_mount_with_other_handler() throws Exception {
        server.mount(MOUNT_DIR, to("/dir")).response(header(HttpHeaders.CONTENT_TYPE, "text/plain"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                org.apache.http.HttpResponse httpResponse = helper.getResponse(remoteUrl("/dir/dir.response"));
                String value = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                assertThat(value, is("text/plain"));
                String content = CharStreams.toString(new InputStreamReader(httpResponse.getEntity().getContent()));
                assertThat(content, is("response from dir"));
            }
        });
    }
}
