package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.MocoRequestHit.times;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMultithreadingTest {
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
    }
    
    @Test
    public void should_work_well_for_request_hit() throws Exception {
        RequestHit hit = requestHit();
        final HttpServer server = httpServer(port(), hit);
        server.get(by(uri("/foo"))).response("bar");

        final ExecutorService executorService = Executors.newFixedThreadPool(50);
        final int count = 100;
        final CountDownLatch latch = new CountDownLatch(count);

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                for (int i = 0; i < count; i ++) {
                    executorService.execute(new java.lang.Runnable() {
                        @Override
                        public void run() {
                            try {
                                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                                latch.countDown();
                            } catch (IOException ignored) {
                            }
                        }
                    });
                }

                latch.await();
            }
        });

        hit.verify(by(uri("/foo")), times(count));
    }
}
