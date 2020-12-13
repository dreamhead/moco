package com.github.dreamhead.moco.recorder;

import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.with;

public class RecorderConfigurations {
    private MocoGroup group;
    private RecorderTape tape;
    private RecorderIdentifier identifier;
    private ReplayModifier modifier;

    public static RecorderConfigurations create(final Iterable<RecorderConfig> configs) {
        RecorderConfigurations configurations = new RecorderConfigurations();

        for (RecorderConfig config : configs) {
            addConfig(configurations, config);
        }

        return configurations;
    }

    private static void addConfig(final RecorderConfigurations configurations,
                                  final RecorderConfig config) {
        if (config.isFor(RecorderConfig.GROUP)) {
            configurations.group = (MocoGroup) config;
        } else if (config.isFor(RecorderConfig.TAPE)) {
            configurations.tape = (RecorderTape) config;
        } else if (config.isFor(RecorderConfig.IDENTIFIER)) {
            configurations.identifier = (RecorderIdentifier) config;
        } else if (config.isFor(RecorderConfig.MODIFIER)) {
            configurations.modifier = (ReplayModifier) config;
        } else {
            throw new IllegalArgumentException("Unknown recorder config:" + config);
        }
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

    public final ReplayModifier getModifier() {
        if (modifier != null) {
            return modifier;
        }

        return new ReplayModifier(with(template("${req.content}")));
    }
}
