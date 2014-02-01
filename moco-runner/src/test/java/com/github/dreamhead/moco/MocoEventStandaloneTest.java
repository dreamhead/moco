package com.github.dreamhead.moco;

import com.github.dreamhead.moco.util.Idles;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.MocoRequestHit.once;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoEventStandaloneTest extends AbstractMocoStandaloneTest {
    private static final int IDLE = 1200;

    private RequestHit hit;
    private HttpServer server;

    @Before
    public void setup() {
        hit = requestHit();
        server = httpserver(2587, hit);
        server.request(by(uri("/target"))).response("0XCAFEBABE");
    }

    @Test
    public void should_fire_event() throws Exception {
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                runWithConfiguration("event.json");
                assertThat(helper.get(remoteUrl("/event")), is("post_foo"));
                Idles.idle(IDLE);
            }
        });
        hit.verify(by(uri("/target")), once());
    }

    @Test
    public void should_fire_get_event() throws Exception {
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                runWithConfiguration("event.json");
                assertThat(helper.get(remoteUrl("/get_event")), is("get_foo"));
                Idles.idle(IDLE);
            }
        });
        hit.verify(by(uri("/target")), once());
    }
}
