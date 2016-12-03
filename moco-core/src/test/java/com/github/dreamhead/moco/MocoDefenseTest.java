package com.github.dreamhead.moco;

import org.apache.http.client.HttpResponseException;
import org.junit.Test;


import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;

public class MocoDefenseTest extends AbstractMocoHttpTest {
    @Test(expected = HttpResponseException.class)
    public void should_work_well_without_response_setting() throws Exception {
        server = httpServer(12306, context("/foo"));
        server.request(by("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postContent(root(), "bar");
            }
        });
    }
}
