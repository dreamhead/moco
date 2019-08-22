package com.github.dreamhead.moco.internal;

public class ServerConfig {
    private static final int MAX_HEADER_SIZE = 8192;

    private int headerSize;

    public ServerConfig(final int headerSize) {
        this.headerSize = headerSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }
}
