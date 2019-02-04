package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.handler.failover.Failover;
import com.github.dreamhead.moco.parser.deserializer.FailoverContainerDeserializer;

import static com.github.dreamhead.moco.Moco.playback;

@JsonDeserialize(using = FailoverContainerDeserializer.class)
public class FailoverContainer {
    private String failover;
    private int[] status;

    private FailoverContainer() {
    }

    public Failover asFailover() {
        if (this.status == null) {
            return Moco.failover(failover);
        }

        return Moco.failover(failover, this.status);
    }

    public Failover asPlayback() {
        if (this.status == null) {
            return playback(failover);
        }

        return playback(failover, this.status);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String file;
        private int[] status;

        public FailoverContainer build() {
            FailoverContainer container = new FailoverContainer();
            container.failover = file;
            container.status = status;
            return container;
        }

        public Builder withFile(final String file) {
            this.file = file;
            return this;
        }

        public Builder withStatus(int[] status) {
            this.status = status;
            return this;
        }
    }
}
