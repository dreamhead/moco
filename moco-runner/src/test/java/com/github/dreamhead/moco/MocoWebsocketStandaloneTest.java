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
import java.io.FileNotFoundException;
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
    public void should_return_connected() throws URISyntaxException, FileNotFoundException {
        runWithConfiguration("websocket/websocket.json");
        final Endpoint endpoint = new Endpoint(new URI("ws://localhost:12306/ws"));
        assertThat(endpoint.getMessageAsText(), is("connected"));
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
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            try {
                this.userSession.getAsyncRemote().sendPing(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
