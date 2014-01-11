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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private String failover;

        private String from;
        private String to;

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder withTo(String to) {
            this.to = to;
            return this;
        }

        public Builder withFailover(String failover) {
            this.failover = failover;
            return this;
        }

        public ProxyContainer build() {
            if (this.url != null && (this.from != null || this.to != null)) {
                throw new IllegalArgumentException("Proxy cannot be set in multiple mode");
            }

            if (this.url == null && (this.from == null || this.to == null)) {
                throw new IllegalArgumentException("Batch proxy needs both 'from' and 'to'");
            }

            return new ProxyContainer(this.url, this.failover, this.from, this.to);
        }
    }
}
