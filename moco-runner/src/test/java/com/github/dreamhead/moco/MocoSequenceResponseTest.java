package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoSequenceResponseTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_responses_in_order() throws IOException {
        runWithConfiguration("sequence_response.json");

        assertThat(helper.get(remoteUrl("/sequence_response")), is("first response"));
        assertThat(helper.get(remoteUrl("/sequence_response")), is("second response"));
    }

    @Test
    public void should_return_last_response_if_count_of_requests_greater_than_count_of_responses() throws IOException {
        runWithConfiguration("sequence_response.json");

        assertThat(helper.get(remoteUrl("/sequence_response")), is("first response"));
        assertThat(helper.get(remoteUrl("/sequence_response")), is("second response"));
        assertThat(helper.get(remoteUrl("/sequence_response")), is("second response"));
    }

    @Test
    public void should_return_response_if_both_response_and_responses_attributes_exist() throws IOException {
        runWithConfiguration("sequence_response.json");

        assertThat(helper.get(remoteUrl("/foo")), is("bar"));
    }

    @Test
    public void should_config_sequence_responses_in_file() throws IOException {
        runWithConfiguration("sequence_response_in_file.json");

        assertThat(helper.get(remoteUrl("/sequence_response")), is("{\"key\":\"first response\"}"));
        assertThat(helper.get(remoteUrl("/sequence_response")), is("{\"key\":\"second response\"}"));
    }
}
