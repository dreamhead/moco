package com.github.dreamhead.moco;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.cors;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoCorsTest extends AbstractMocoHttpTest {
    @Test
    public void should_support_cors() throws Exception {
        server.response(cors());

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponse(root());
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        });
    }
}
