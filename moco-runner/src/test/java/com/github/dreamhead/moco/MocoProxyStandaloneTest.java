package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoProxyStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_response_with_proxy() throws IOException {
        runWithConfiguration("proxy.json");
        String content = helper.get(remoteUrl("/proxy"));
        assertThat(content, is("proxy_target"));
    }

    @Test
    public void should_failover() throws IOException {
        runWithConfiguration("proxy.json");
        String content = helper.postContent(remoteUrl("/failover"), "proxy");
        assertThat(content, is("proxy"));
    }

    @Test
    public void should_playback() throws IOException {
        runWithConfiguration("proxy.json");
        String content = helper.postContent(remoteUrl("/playback"), "proxy");
        assertThat(content, is("proxy"));
    }

    @Test
    public void should_batch_proxy() throws IOException {
        runWithConfiguration("proxy_batch.json");
        String content1 = helper.get(remoteUrl("/proxy/1"));
        assertThat(content1, is("target_1"));
        String content2 = helper.get(remoteUrl("/proxy/2"));
        assertThat(content2, is("target_2"));
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_if_proxy_has_both_url_and_batch() {
        runWithConfiguration("proxy_error_multiple_mode.json");
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_if_from_is_missing() {
        runWithConfiguration("proxy_error_from_missing.json");
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_if_to_is_missing() {
        runWithConfiguration("proxy_error_to_missing.json");
    }

    @Test
    public void should_failover_for_batch_proxy() throws IOException {
        runWithConfiguration("proxy_batch.json");
        String content = helper.postContent(remoteUrl("/failover/1"), "proxy");
        assertThat(content, is("proxy"));
    }

    @Test
    public void should_batch_proxy_from_server() throws IOException {
        runWithConfiguration("proxy_server.json");
        String content1 = helper.get(remoteUrl("/proxy/1"));
        assertThat(content1, is("target_1"));
        String content2 = helper.get(remoteUrl("/proxy/2"));
        assertThat(content2, is("target_2"));
    }

    @Test
    public void should_batch_proxy_with_failover_from_server() throws IOException {
        runWithConfiguration("proxy_server.json");
        String content = helper.postContent(remoteUrl("/failover/1"), "proxy");
        assertThat(content, is("proxy"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_proxy_from_server_with_url() throws IOException {
        runWithConfiguration("proxy_error_url_from_server.json");
    }

    @Test
    public void should_response_with_proxy_template_url() throws IOException {
        runWithConfiguration("proxy.json");
        String content = helper.get(remoteUrl("/template-url?foo=target"));
        assertThat(content, is("proxy_target"));
    }

    @Test
    public void should_response_with_failover_status() throws IOException {
        runWithConfiguration("proxy.json");
        String content = helper.get(remoteUrl("/failover-with-status"));
        assertThat(content, is("proxy"));

        content = helper.get(remoteUrl("/failover-with-status"));
        assertThat(content, is("proxy"));
    }
}
