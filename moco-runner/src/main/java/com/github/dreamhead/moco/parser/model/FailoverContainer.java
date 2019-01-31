package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.parser.deserializer.FailoverContainerDeserializer;

@JsonDeserialize(using = FailoverContainerDeserializer.class)
public class FailoverContainer {
    private String failover;

    public FailoverContainer(final String failover) {
        this.failover = failover;
    }

    public String getFailover() {
        return failover;
    }
}
