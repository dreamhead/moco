package com.github.dreamhead.moco.internal;

public class ServerConfig {
    private int headerSize;

    public ServerConfig(final int headerSize) {
        this.headerSize = headerSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }
}
