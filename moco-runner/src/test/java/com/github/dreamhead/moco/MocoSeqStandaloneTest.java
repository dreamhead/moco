package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoSeqStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_work_well() throws IOException {
        runWithConfiguration("seq.json");
        assertThat(helper.get(root()), is("foo"));
        assertThat(helper.get(root()), is("bar"));
        assertThat(helper.get(root()), is("bar"));
    }
}
