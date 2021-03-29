package com.github.dreamhead.moco;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoWebsocketStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_connected() throws URISyntaxException {
        runWithConfiguration("websocket/websocket.json");
        final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        assertThat(endpoint.getMessageAsText(), is("connected"));
        endpoint.ping("ping");
        assertThat(endpoint.getMessageAsText(), is("pong"));
        endpoint.sendTextMessage("foo");
        assertThat(endpoint.getMessageAsText(), is("bar"));
    }

    @Test
    public void should_return_specified() throws URISyntaxException {
        runWithConfiguration("websocket/websocket.json");
        final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        assertThat(endpoint.getMessageAsText(), is("connected"));
        endpoint.sendTextMessage("foo.request");
        assertThat(endpoint.getMessageAsText(), is("foo.response"));
    }

    @Test
    public void should_broadcast() throws URISyntaxException {
        runWithConfiguration("websocket/websocket_with_broadcast.json");
        final Endpoint subEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        final Endpoint broadcast = new Endpoint(new URI("ws://localhost:12306/ws"));
        broadcast.sendTextMessage("broadcast");
        assertThat(broadcast.getMessageAsText(), is("login"));
        assertThat(subEndpoint.getMessageAsText(), is("login"));
    }

    @Test
    public void should_broadcast_with_text() throws URISyntaxException {
        runWithConfiguration("websocket/websocket_with_broadcast.json");
        final Endpoint fooEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        final Endpoint subscribeEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        subscribeEndpoint.sendTextMessage("broadcast2");
        assertThat(fooEndpoint.getMessageAsText(), is("login2"));
        assertThat(subscribeEndpoint.getMessageAsText(), is("login2"));
    }

    @Test
    public void should_broadcast_with_file() throws URISyntaxException {
        runWithConfiguration("websocket/websocket_with_broadcast.json");
        final Endpoint fooEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        final Endpoint subscribeEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        subscribeEndpoint.sendTextMessage("broadcast-with-file");
        assertThat(fooEndpoint.getMessageAsText(), is("foo.response"));
    }

    @Test
    public void should_broadcast_with_group() throws URISyntaxException {
        runWithConfiguration("websocket/websocket_with_broadcast.json");
        final Endpoint fooEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        final Endpoint subscribeEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        subscribeEndpoint.sendTextMessage("subscribe-with-group");
        assertThat(subscribeEndpoint.getMessageAsText(), is("subscribed"));
        subscribeEndpoint.clearMessage();
        fooEndpoint.sendTextMessage("broadcast-with-group");
        assertThat(subscribeEndpoint.getMessageAsText(), is("broadcast-content"));
    }
}
