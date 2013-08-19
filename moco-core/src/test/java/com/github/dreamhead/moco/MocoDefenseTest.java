package com.github.dreamhead.moco;

import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;

public class MocoDefenseTest extends AbstractMocoTest {
    @Test(expected = HttpResponseException.class)
    public void should_work_well_without_response_setting() throws Exception {
        server = httpserver(12306, context("/foo"));
        server.request(by("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postContent(root(), "bar");
            }
        });
    }
}
