package com.github.dreamhead.moco.internal;

public class ServerConfig {
    private final int headerSize;
    private final int contentLength;

    public ServerConfig(final int maxHeaderSize, final int maxContentLength) {
        this.headerSize = maxHeaderSize;
        this.contentLength = maxContentLength;
    }

    public final int getHeaderSize() {
        return headerSize;
    }

    public final int getContentLength() {
        return contentLength;
    }
}
