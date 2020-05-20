package com.github.dreamhead.moco.internal;

public class ServerConfig {
    private int headerSize;
    private int contentLength;

    public ServerConfig(final int maxHeaderSize, final int maxContentLength) {
        this.headerSize = maxHeaderSize;
        this.contentLength = maxContentLength;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public int getContentLength() {
        return contentLength;
    }
}
