package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.parser.deserializer.FailoverContainerDeserializer;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.playback;

@JsonDeserialize(using = FailoverContainerDeserializer.class)
public final class FailoverContainer {
    private String file;
    private int[] status;

    private FailoverContainer() {
    }

    public Failover asFailover() {
        if (this.status == null) {
            return Moco.failover(file);
        }

        return Moco.failover(file, this.status);
    }

    public Failover asPlayback() {
        if (this.status == null) {
            return playback(file);
        }

        return playback(file, this.status);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("file", file)
                .add("status", status)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String file;
        private int[] status;

        public FailoverContainer build() {
            FailoverContainer container = new FailoverContainer();
            container.file = file;
            container.status = status;
            return container;
        }

        public Builder withFile(final String file) {
            this.file = file;
            return this;
        }

        public Builder withStatus(final int[] status) {
            this.status = status;
            return this;
        }
    }
}
