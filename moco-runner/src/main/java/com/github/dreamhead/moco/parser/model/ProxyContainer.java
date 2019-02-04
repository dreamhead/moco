package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.handler.proxy.ProxyConfig;
import com.github.dreamhead.moco.parser.deserializer.ProxyContainerDeserializer;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.from;

@JsonDeserialize(using = ProxyContainerDeserializer.class)
public class ProxyContainer {
    private TextContainer url;
    private String from;
    private String to;

    private FailoverContainer failover;
    private FailoverContainer playback;

    public final boolean hasUrl() {
        return url != null;
    }

    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this)
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

    public final Failover getFailover() {
        if (failover != null) {
            return failover.asFailover();
        }

        if (playback != null) {
            return playback.asPlayback();
        }

        return Failover.DEFAULT_FAILOVER;
    }

    public final ProxyConfig getProxyConfig() {
        return from(from).to(to);
    }

    private boolean hasProxyConfig() {
        return from != null && to != null;
    }

    public final ResponseHandler asResponseHandler() {
        Failover failover = getFailover();

        if (hasProxyConfig()) {
            return Moco.proxy(getProxyConfig(), failover);
        }

        return Moco.proxy(url.asResource(), failover);
    }

    public static class Builder {
        private TextContainer url;
        private FailoverContainer failover;
        private FailoverContainer playback;

        private String from;
        private String to;

        public final Builder withUrl(final String url) {
            this.url = TextContainer.builder().withText(url).build();
            return this;
        }

        public final Builder withUrl(final TextContainer container) {
            this.url = container;
            return this;
        }

        public final Builder withFrom(final String from) {
            this.from = from;
            return this;
        }

        public final Builder withTo(final String to) {
            this.to = to;
            return this;
        }

        public final Builder withFailover(final FailoverContainer failover) {
            this.failover = failover;
            return this;
        }

        public final Builder withPlayback(final FailoverContainer playback) {
            this.playback = playback;
            return this;
        }

        public final ProxyContainer build() {
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
