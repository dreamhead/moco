package com.github.dreamhead.moco;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ClientEndpoint
public class Endpoint {
    private Session userSession;
    private CompletableFuture<byte[]> message;

    public Endpoint(final URI uri) {
        try {
            clearMessage();
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
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

    public byte[] getMessage() {
        try {
            return message.get(10, TimeUnit.SECONDS);
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
