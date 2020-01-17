package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.parser.deserializer.ReplayContainerDeserializer;

@JsonDeserialize(using = ReplayContainerDeserializer.class)
public class ReplayContainer {
    private TextContainer identifier;

    public ReplayContainer(final TextContainer identifier) {
        this.identifier = identifier;
    }

    public TextContainer getIdentifier() {
        return identifier;
    }
}
