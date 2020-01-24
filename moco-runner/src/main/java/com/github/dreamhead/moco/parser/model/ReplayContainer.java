package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.MocoRecorders;
import com.github.dreamhead.moco.parser.deserializer.ReplayContainerDeserializer;
import com.github.dreamhead.moco.recorder.RecorderConfig;
import com.github.dreamhead.moco.recorder.RecorderIdentifier;
import com.github.dreamhead.moco.recorder.RecorderModifier;

import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.Moco.template;

@JsonDeserialize(using = ReplayContainerDeserializer.class)
public class ReplayContainer {
    private String group;
    private TextContainer identifier;
    private String modifier;

    public ReplayContainer(final String group, final TextContainer identifier, final String modifier) {
        this.group = group;
        this.identifier = identifier;
        this.modifier = modifier;
    }

    public final RecorderConfig[] getConfigs() {
        List<RecorderConfig> configs = new ArrayList<>();
        if (group != null) {
            configs.add(MocoRecorders.group(this.group));
        }

        configs.add(getIdentifier());
        configs.add(getModifier());

        return configs.toArray(new RecorderConfig[0]);
    }

    private RecorderModifier getModifier() {
        if (modifier != null) {
            return MocoRecorders.modifier(modifier);
        }

        return new RecorderModifier(template("${req.content}"));
    }

    private RecorderIdentifier getIdentifier() {
        return MocoRecorders.identifier(identifier.asResource());
    }
}
