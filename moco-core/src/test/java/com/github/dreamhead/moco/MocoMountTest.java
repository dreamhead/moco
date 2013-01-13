package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.MocoMount.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.ResourceFiles.newResourceFile;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMountTest {
    private HttpServer server;
    private MocoTestHelper helper;
    private String mountDir;

    @Before
    public void setUp() throws Exception {
        server = httpserver(port());
        helper = new MocoTestHelper();
        File tmpDir = Files.createTempDir();
        newResourceFile("test/dir.response", tmpDir);
        newResourceFile("test/foo.bar", tmpDir);
        mountDir = tmpDir.getAbsolutePath();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_dir_does_not_exist() {
        server.mount("unknown_dir", to("/dir"));
    }

    @Test
    public void should_mount_dir_to_uri() {
        server.mount(mountDir, to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/dir/dir.response")), is("response from dir"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_return_bad_request_for_nonexistence_file() {
        server.mount(mountDir, to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    helper.get(remoteUrl("/dir/unknown.response"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_inclusion_file() {
        server.mount(mountDir, to("/dir"), include("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/dir/dir.response")), is("response from dir"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_not_return_non_inclusion_file() {
        server.mount(mountDir, to("/dir"), include("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    helper.get(remoteUrl("/dir/foo.bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_not_return_exclusion_file() {
        server.mount(mountDir, to("/dir"), exclude("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    helper.get(remoteUrl("/dir/dir.response"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_non_exclusion_file() {
        server.mount(mountDir, to("/dir"), exclude("*.response"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/dir/foo.bar")), is("foo.bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
