package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.JsonRunner;
import com.google.common.io.Resources;
import org.apache.http.client.HttpResponseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMountStandaloneTest {
    private static int PORT = 9090;
    private final MocoTestHelper helper = new MocoTestHelper();
    private JsonRunner runner;

    @Before
    public void setup() throws IOException {
        runner = new JsonRunner();
    }

    @After
    public void teardown() {
        runner.stop();
    }

    private void runWithConfiguration(String resourceName, int port) {
        try {
            runner.run(Resources.getResource(resourceName).openStream(), port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_mount_dir_to_uri() throws IOException {
        runWithConfiguration("mount.json", PORT);
        assertThat(helper.get("http://localhost:9090/mount/mount.response"), is("response from mount"));
    }

    @Test
    public void should_mount_dir_to_uri_with_include() throws IOException {
        runWithConfiguration("mount.json", PORT);
        assertThat(helper.get("http://localhost:9090/mount-include/mount.response"), is("response from mount"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_non_inclusion() throws IOException {
        runWithConfiguration("mount.json", PORT);
        helper.get("http://localhost:9090/mount-include/foo.bar");
    }

    @Test
    public void should_mount_dir_to_uri_with_exclude() throws IOException {
        runWithConfiguration("mount.json", PORT);
        assertThat(helper.get("http://localhost:9090/mount-exclude/foo.bar"), is("foo.bar"));
    }

    @Test(expected = HttpResponseException.class)
    public void should_return_exclusion() throws IOException {
        runWithConfiguration("mount.json", PORT);
        helper.get("http://localhost:9090/mount-exclude/mount.response");
    }
}
