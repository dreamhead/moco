package com.github.dreamhead.moco.internal;

import com.google.common.base.MoreObjects;

import java.net.InetSocketAddress;

public class Client {
    private final String address;
    private final int port;

    public Client(final InetSocketAddress address) {
        this.address = address.getAddress().getHostAddress();
        this.port = address.getPort();
    }

    public final String getAddress() {
        return address;
    }

    public final int getPort() {
        return port;
    }

    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this)
                .add("address", address)
                .add("port", port)
                .toString();
    }
}
