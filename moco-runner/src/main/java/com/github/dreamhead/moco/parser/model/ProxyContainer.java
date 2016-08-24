package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.parser.deserializer.ProxyContainerDeserializer;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.failover;
import static com.github.dreamhead.moco.Moco.playback;
import static com.github.dreamhead.moco.Moco.from;

@JsonDeserialize(using = ProxyContainerDeserializer.class)
public class ProxyContainer {
    private String url;
    private String from;
    private String to;

    private String failover;
    private String playback;

    public boolean hasUrl() {
        return url != null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(ProxyContainer.class)
                .omitNullValues()
                .add("url", this.url)
                .add("from", this.from)
                .add("to", this.to)
                .add("failover", this.failover)
                .add("playback", this.playback)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Failover getFailover() {
        if (failover != null) {
            return failover(failover);
        }

        if (playback != null) {
            return playback(playback);
        }

        return Failover.DEFAULT_FAILOVER;
    }

    public ProxyConfig getProxyConfig() {
        return from(from).to(to);
    }

    private boolean hasProxyConfig() {
        return from != null && to != null;
    }

    public ResponseHandler asResponseHandler() {
        Failover failover = getFailover();

        if (hasProxyConfig()) {
            return Moco.proxy(getProxyConfig(), failover);
        }

        return Moco.proxy(url, failover);
    }

    public static class Builder {
        private String url;
        private String failover;
        private String playback;

        private String from;
        private String to;

        public Builder withUrl(final String url) {
            this.url = url;
            return this;
        }

        public Builder withFrom(final String from) {
            this.from = from;
            return this;
        }

        public Builder withTo(final String to) {
            this.to = to;
            return this;
        }

        public Builder withFailover(final String failover) {
            this.failover = failover;
            return this;
        }

        public Builder withPlayback(final String playback) {
            this.playback = playback;
            return this;
        }

        public ProxyContainer build() {
            if (this.url != null && (this.from != null || this.to != null)) {
                throw new IllegalArgumentException("Proxy cannot be set in multiple mode");
            }

            if (this.url == null && (this.from == null || this.to == null)) {
                throw new IllegalArgumentException("Batch proxy needs both 'from' and 'to'");
            }

            ProxyContainer container = new ProxyContainer();
            container.url = url;
            container.from = from;
            container.to = to;
            container.failover = failover;
            container.playback = playback;
            return container;
        }
    }
}
