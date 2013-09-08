package com.github.dreamhead.moco;

import org.junit.Test;

import static com.github.dreamhead.moco.Moco.complete;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MocoEventTest extends AbstractMocoTest {
    @Test
    public void should_fire_event_on_complete() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        server.response("foo").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foo"));
            }
        });

        verify(action).execute();
    }
}
