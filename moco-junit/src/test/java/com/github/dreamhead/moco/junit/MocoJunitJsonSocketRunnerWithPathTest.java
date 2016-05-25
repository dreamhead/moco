package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.local;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJunitJsonSocketRunnerWithPathTest {
    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonSocketRunner(12306, pathResource("base.json"));

    private MocoSocketHelper helper;

    @Before
    public void setup() {
        this.helper = new MocoSocketHelper(local(), port());
    }

    @Test
    public void should_return_expected_response() throws Exception {
        helper.connect();
        assertThat(helper.send("foo", 3), is("bar"));
        helper.close();
    }

}
