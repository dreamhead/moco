package com.github.dreamhead.moco;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static com.github.dreamhead.moco.Moco.binary;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.group;
import static com.github.dreamhead.moco.Moco.join;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.MocoWebSockets.broadcast;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class MocoWebsocketTest extends AbstractMocoHttpTest {
    private WebSocketServer webSocketServer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        webSocketServer = server.websocket("/ws");
    }

    @Test
    public void should_connect() throws Exception {
        webSocketServer.connected(text("hello"));

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            assertThat(endpoint.getMessageAsText(), is("hello"));
        });
    }

    @Test
    public void should_connect_with_text() throws Exception {
        webSocketServer.connected("hello");

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            assertThat(endpoint.getMessageAsText(), is("hello"));
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_fail_to_connect_with_unknown_uri() throws Exception {
        webSocketServer.connected("hello");

        running(server, () -> {
            new Endpoint(new URI("ws://localhost:12306/unknown/"));
        });
    }

    @Test
    public void should_response_based_on_request() throws Exception {
        webSocketServer.request(by("foo")).response("bar");

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.sendTextMessage("foo");
            assertThat(endpoint.getMessageAsText(), is("bar"));
        });
    }

    @Test
    public void should_response_any_response() throws Exception {
        webSocketServer.request(by("foo")).response("bar");
        webSocketServer.response("any");

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.sendTextMessage("blah");
            assertThat(endpoint.getMessageAsText(), is("any"));
        });
    }

    @Test
    public void should_binary_response_based_on_binary_request() throws Exception {
        webSocketServer.request(by(binary(new byte[] {1, 2, 3}))).response(binary(new byte[] {4, 5, 6}));

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.sendBinaryMessage(new byte[] {1, 2, 3});
            assertThat(endpoint.getMessage(), is(new byte[] {4, 5, 6}));
        });
    }

    @Test
    public void should_pong_based_on_ping() throws Exception {
        webSocketServer.request(by(binary(new byte[] {1, 2, 3}))).response(binary(new byte[] {4, 5, 6}));
        webSocketServer.ping("hello").pong("world");

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.ping("hello");
            assertThat(endpoint.getMessage(), is("world".getBytes()));
        });
    }

    @Test
    public void should_pong_based_on_ping_resource() throws Exception {
        webSocketServer.request(by(binary(new byte[] {1, 2, 3}))).response(binary(new byte[] {4, 5, 6}));
        webSocketServer.ping(text("hello")).pong(text("world"));

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.ping("hello");
            assertThat(endpoint.getMessage(), is("world".getBytes()));
        });
    }

    @Test
    public void should_pong_based_on_ping_matcher() throws Exception {
        webSocketServer.request(by(binary(new byte[] {1, 2, 3}))).response(binary(new byte[] {4, 5, 6}));
        webSocketServer.ping(by("hello")).pong("world");

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.ping("hello");
            assertThat(endpoint.getMessage(), is("world".getBytes()));
        });
    }

    @Test
    public void should_pong_with_response_handler_based_on_ping() throws Exception {
        webSocketServer.request(by(binary(new byte[] {1, 2, 3}))).response(binary(new byte[] {4, 5, 6}));
        webSocketServer.ping("hello").pong(with(text("world")));

        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.ping("hello");
            assertThat(endpoint.getMessage(), is("world".getBytes()));
        });
    }

    @Test
    public void should_broadcast() throws Exception {
        webSocketServer.request(by("foo")).response(broadcast("bar"));
        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            final Endpoint endpoint2 = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.sendTextMessage("foo");

            assertThat(endpoint.getMessage(), is("bar".getBytes()));
            assertThat(endpoint2.getMessage(), is("bar".getBytes()));
        });
    }

    @Test
    public void should_broadcast_with_resource() throws Exception {
        webSocketServer.request(by("foo")).response(broadcast(text("bar")));
        running(server, () -> {
            final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
            final Endpoint endpoint2 = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpoint.sendTextMessage("foo");

            assertThat(endpoint.getMessage(), is("bar".getBytes()));
            assertThat(endpoint2.getMessage(), is("bar".getBytes()));
        });
    }

    @Test
    public void should_broadcast_with_group() throws Exception {
        webSocketServer.request(by("subscribeFoo")).response(with("fooSubscribed"), join(group("foo")));
        webSocketServer.request(by("subscribeBar")).response(with("barSubscribed"), join(group("bar")));
        webSocketServer.request(by("foo")).response(broadcast(text("foo"), group("foo")));
        running(server, () -> {
            final Endpoint endpointFoo = new Endpoint(new URI("ws://localhost:12306/ws"));
            final Endpoint endpointBar = new Endpoint(new URI("ws://localhost:12306/ws"));
            endpointFoo.sendTextMessage("subscribeFoo");
            endpointBar.sendTextMessage("subscribeBar");
            assertThat(endpointFoo.getMessage(), is("fooSubscribed".getBytes()));
            assertThat(endpointBar.getMessage(), is("barSubscribed".getBytes()));

            endpointBar.clearMessage();

            endpointFoo.sendTextMessage("foo");
            assertThat(endpointFoo.getMessage(), is("foo".getBytes()));

            try {
                endpointBar.getMessage();
                fail();
            } catch (IllegalStateException e) {
                // ignored
            }
        });
    }

}
