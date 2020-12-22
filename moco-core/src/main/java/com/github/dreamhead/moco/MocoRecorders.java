package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.DynamicRecordHandler;
import com.github.dreamhead.moco.recorder.DynamicReplayHandler;
import com.github.dreamhead.moco.recorder.RecorderConfig;
import com.github.dreamhead.moco.recorder.RecorderConfigurations;
import com.github.dreamhead.moco.recorder.RecorderIdentifier;
import com.github.dreamhead.moco.recorder.RecorderTape;
import com.github.dreamhead.moco.recorder.ReplayModifier;
import com.github.dreamhead.moco.resource.ContentResource;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoRecorders {
    public static ResponseHandler record(final RecorderConfig config, final RecorderConfig... configs) {
        RecorderConfigurations configurations = RecorderConfigurations.create(
                asIterable(checkNotNull(config, "Configuration should not be null"),
                        checkNotNull(configs, "Configuration should not be null")));
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final RecorderConfig config, final RecorderConfig... configs) {
        return new DynamicReplayHandler(RecorderConfigurations.create(
                asIterable(checkNotNull(config, "Configuration should not be null"),
                        checkNotNull(configs, "Configuration should not be null"))));
    }

    public static RecorderTape tape(final String path) {
        return new RecorderTape(checkNotNullOrEmpty(path, "tape should not be empty"));
    }

    public static RecorderIdentifier identifier(final String text) {
        return new RecorderIdentifier(template(checkNotNullOrEmpty(text, "Identifier should not be empty")));
    }

    public static RecorderIdentifier identifier(final ContentResource text) {
        return new RecorderIdentifier(checkNotNull(text, "Identifier should not be empty"));
    }

    public static ReplayModifier modifier(final String text) {
        return new ReplayModifier(with(template(checkNotNullOrEmpty(text, "Modifier should not be empty"))));
    }

    public static ReplayModifier modifier(final ResponseElement element, final ResponseElement... elements) {
        return new ReplayModifier(and(checkNotNull(element, "Response should not be null"),
                checkNotNull(elements, "Responses should not be null")));
    }

    private MocoRecorders() {
    }
}
