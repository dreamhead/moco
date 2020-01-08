package com.github.dreamhead.moco.recorder;

import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.text;

public class RecorderConfigurations {
    private RecorderGroup group;
    private RecorderTape tape;
    private RecorderIdentifier identifier;
    private RecorderModifier modifier;

    public static RecorderConfigurations create(final Iterable<RecorderConfig> configs) {
        RecorderConfigurations configurations = new RecorderConfigurations();

        for (RecorderConfig config : configs) {
            if (config.isFor(RecorderConfig.GROUP)) {
                configurations.group = (RecorderGroup) config;
            } else if (config.isFor(RecorderConfig.TAPE)) {
                configurations.tape = (RecorderTape) config;
            } else if (config.isFor(RecorderConfig.IDENTIFIER)) {
                configurations.identifier = (RecorderIdentifier) config;
            } else if (config.isFor(RecorderConfig.MODIFIER)) {
                configurations.modifier = (RecorderModifier)config;
            } else {
                throw new IllegalArgumentException("Unknown recorder config:" + config);
            }
        }

        return configurations;
    }

    public final RecorderRegistry getRecorderRegistry() {
        if (group != null) {
            return RecorderRegistry.registryOf(group.getName(), getRecordFactory());
        }

        return RecorderRegistry.defaultRegistry();
    }

    private RecorderFactory getRecordFactory() {
        if (tape != null) {
            return new TapeRecorderFactory(tape);
        }

        return RecorderFactory.IN_MEMORY;
    }

    public final RecorderIdentifier getIdentifier() {
        if (identifier != null) {
            return identifier;
        }


        if (group != null) {
            return new RecorderIdentifier(text(group.getName()));
        }

        throw new IllegalArgumentException("No identifier found");
    }

    public final RecorderModifier getModifier() {
        if (modifier != null) {
            return modifier;
        }

        return new RecorderModifier(template("${req.content}"));
    }
}
