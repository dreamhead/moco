package com.github.dreamhead.moco;

import org.apache.hc.client5.http.HttpResponseException;
import org.junit.jupiter.api.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MocoDefenseTest extends AbstractMocoHttpTest {
    @Test
    public void should_work_well_without_response_setting() throws Exception {
        server = httpServer(12306, context("/foo"));
        server.request(by("bar"));

        assertThrows(HttpResponseException.class, () ->
                running(server, () -> helper.postContent(root(), "bar")));
    }
}
