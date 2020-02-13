package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.MocoRecorders;
import com.github.dreamhead.moco.parser.deserializer.ReplayContainerDeserializer;
import com.github.dreamhead.moco.recorder.RecorderConfig;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ReplayContainerDeserializer.class)
public class ReplayContainer {
    private String group;
    private TextContainer identifier;
    private ReplayModifierContainer modifier;
    private String tape;

    public ReplayContainer(final String group,
                           final TextContainer identifier,
                           final ReplayModifierContainer modifier,
                           final String tape) {
        this.group = group;
        this.identifier = identifier;
        this.modifier = modifier;
        this.tape = tape;
    }

    public final RecorderConfig[] getConfigs() {
        List<RecorderConfig> configs = new ArrayList<>();
        if (group != null) {
            configs.add(MocoRecorders.group(this.group));
        }

        if (identifier != null) {
            configs.add(MocoRecorders.identifier(identifier.asResource()));
        }

        if (modifier != null) {
            configs.add(MocoRecorders.modifier(modifier.getResponseHandler()));
        }

        if (tape != null) {
            configs.add(MocoRecorders.tape(this.tape));
        }

        return configs.toArray(new RecorderConfig[0]);
    }
}
