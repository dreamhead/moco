package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

public class ProxyContainer {
    private final String url;
    private final String failover;

    private final String from;
    private final String to;

    public ProxyContainer(String url, String failover, String from, String to) {
        this.url = url;
        this.failover = failover;
        this.from = from;
        this.to = to;
    }

    public String getUrl() {
        return url;
    }

    public String getFailover() {
        return failover;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ProxyContainer.class)
                .add("url", this.url)
                .add("failover", this.failover)
                .toString();
    }

    public static ProxyContainer batchProxy(String from, String to) {
        return new ProxyContainer(null, null, from, to);
    }
}
