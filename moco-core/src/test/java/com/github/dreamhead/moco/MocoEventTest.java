package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.util.Idles;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.*;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class MocoEventTest extends AbstractMocoHttpTest {
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

    @Test
    public void should_not_fire_event_on_specific_request() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        server.request(by(uri("/event"))).response("event").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(action).execute();
    }

    @Test
    public void should_not_fire_event_on_other_request() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        server.request(by(uri("/noevent"))).response("noevent");
        server.request(by(uri("/event"))).response("foo").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/noevent")), is("noevent"));
            }
        });

        verify(action, Mockito.never()).execute();
    }

    @Test
    public void should_send_get_request_to_target_on_complete() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(get(remoteUrl("/target"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(Matchers.<SessionContext>anyObject());
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_string() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target")), by("content")).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), "content")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(Matchers.<SessionContext>anyObject());
    }

    @Test
    public void should_send_post_request_to_target_on_complete() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target")), by("content")).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), text("content"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(Matchers.<SessionContext>anyObject());
    }

    @Test
    public void should_send_post_request_to_target_on_complete_asyc() throws Exception {
        final ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target")), by("content")).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(async(post(remoteUrl("/target"), text("content")))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
                verify(handler, never()).writeToResponse(Matchers.<SessionContext>anyObject());
                Idles.idle(2, TimeUnit.SECONDS);
            }
        });

        verify(handler).writeToResponse(Matchers.<SessionContext>anyObject());
    }

    @Test
    public void should_send_post_request_to_target_on_complete_async_after_awhile() throws Exception {
        final ResponseHandler handler = mock(ResponseHandler.class);
        server.request(by(uri("/target")), by("content")).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(async(post(remoteUrl("/target"), text("content")), latency(1, TimeUnit.SECONDS))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
                verify(handler, never()).writeToResponse(Matchers.<SessionContext>anyObject());
                Idles.idle(2, TimeUnit.SECONDS);
            }
        });

        verify(handler).writeToResponse(Matchers.<SessionContext>anyObject());
    }

    @Test
    public void should_fire_event_for_context_configuration() throws Exception {
        MocoEventAction action = mock(MocoEventAction.class);
        when(action.apply(Matchers.<MocoConfig>anyObject())).thenReturn(action);
        server = httpServer(port(), context("/context"));
        server.get(by(uri("/foo"))).response("foo").on(complete(action));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/context/foo")), is("foo"));
            }
        });

        verify(action).execute();
    }

    @Test
    public void should_send_post_request_with_file_root_configuration() throws Exception {
        ResponseHandler handler = mock(ResponseHandler.class);
        when(handler.apply(Matchers.<MocoConfig>anyObject())).thenReturn(handler);

        server = httpServer(port(), fileRoot("src/test/resources"));
        server.request(by(uri("/target")), by(file("foo.request"))).response(handler);
        server.request(by(uri("/event"))).response("event").on(complete(post(remoteUrl("/target"), file("foo.request"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/event")), is("event"));
            }
        });

        verify(handler).writeToResponse(Matchers.<SessionContext>anyObject());
    }
}
