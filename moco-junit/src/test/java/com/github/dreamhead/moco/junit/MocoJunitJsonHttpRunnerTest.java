package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJunitJsonHttpRunnerTest extends AbstractMocoStandaloneTest {
    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonHttpRunner(12306, "src/test/resources/foo.json");

    @Test
    public void should_return_expected_message() throws IOException {
        assertThat(helper.get(root()), is("foo"));
    }

    @Test
    public void should_return_expected_message_2() throws IOException {
        assertThat(helper.get(root()), is("foo"));
    }
}
