package com.github.dreamhead.moco;

import org.junit.Test;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    }

    @Test
    public void should_broadcast_with_file() throws URISyntaxException {
        runWithConfiguration("websocket/websocket_with_broadcast.json");
        final Endpoint fooEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        final Endpoint subscribeEndpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        subscribeEndpoint.sendTextMessage("broadcast-with-file");
        assertThat(fooEndpoint.getMessageAsText(), is("foo.response"));
    }

    @ClientEndpoint
    public static class Endpoint {
        private Session userSession;
        private CompletableFuture<byte[]> message;

        public Endpoint(final URI uri) {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, uri);
                clearMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @OnOpen
        public void onOpen(final Session userSession) {
            this.userSession = userSession;
        }

        @OnClose
        public void onClose(final Session userSession, final CloseReason reason) {
            this.userSession = null;
        }

        @OnMessage
        public void onMessage(final byte[] message) {
            this.message.complete(message);
        }

        @OnMessage
        public void onPong(final PongMessage message) {
            this.message.complete(message.getApplicationData().array());
        }

        public void sendTextMessage(final String message) {
            clearMessage();
            this.userSession.getAsyncRemote().sendText(message);
        }

        public void sendBinaryMessage(final byte[] message) {
            clearMessage();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            this.userSession.getAsyncRemote().sendBinary(buffer);
        }

        public void clearMessage() {
            this.message = new CompletableFuture<>();
        }

        public String getMessageAsText() {
            return new String(getMessage());
        }

        private byte[] getMessage() {
            try {
                return message.get(2, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                return new byte[0];
            } catch (TimeoutException e) {
                throw new IllegalStateException("No message found", e);
            }
        }

        public void ping(final String message) {
            clearMessage();
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            try {
                this.userSession.getAsyncRemote().sendPing(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
