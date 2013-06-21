package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

public class ProxyContainer {
    private final String url;
    private final String failover;

    public ProxyContainer(String url, String failover) {
        this.url = url;
        this.failover = failover;
    }

    public String getUrl() {
        return url;
    }

    public String getFailover() {
        return failover;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ProxyContainer.class)
                .add("url", this.url)
                .add("failover", this.failover)
                .toString();
    }
}
