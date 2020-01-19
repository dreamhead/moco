package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.MocoRecorders;
import com.github.dreamhead.moco.parser.deserializer.ReplayContainerDeserializer;
import com.github.dreamhead.moco.recorder.RecorderModifier;

import static com.github.dreamhead.moco.Moco.template;

@JsonDeserialize(using = ReplayContainerDeserializer.class)
public class ReplayContainer {
    private TextContainer identifier;
    private String modifier;

    public ReplayContainer(final TextContainer identifier, final String modifier) {
        this.identifier = identifier;
        this.modifier = modifier;
    }

    public TextContainer getIdentifier() {
        return identifier;
    }

    public RecorderModifier getModifier() {
        if (modifier != null) {
            return MocoRecorders.modifier(modifier);
        }

        return new RecorderModifier(template("${req.content}"));
    }
}
