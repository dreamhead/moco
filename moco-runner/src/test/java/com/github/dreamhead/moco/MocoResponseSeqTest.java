package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoResponseSeqTest extends AbstractMocoStandaloneTest {

    @Test
    public void should_return_expected_response_seq() throws IOException {
        runWithConfiguration("response_seq.json");
        assertThat(helper.get(root()), is("bar"));
        assertThat(helper.get(root()), is("response from path"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_response_and_response_seq_are_specified_at_same_time() throws IOException {
        runWithConfiguration("response_and_seq.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_response_seq_is_empty() throws IOException {
        runWithConfiguration("response_empty_seq.json");
    }
}
