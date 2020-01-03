package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.DynamicRecordHandler;
import com.github.dreamhead.moco.recorder.DynamicReplayHandler;
import com.github.dreamhead.moco.recorder.RecorderConfig;
import com.github.dreamhead.moco.recorder.RecorderConfigurations;
import com.github.dreamhead.moco.recorder.RecorderGroup;
import com.github.dreamhead.moco.recorder.RecorderIdentifier;
import com.github.dreamhead.moco.recorder.RecorderModifier;
import com.github.dreamhead.moco.recorder.RecorderTape;
import com.github.dreamhead.moco.recorder.RequestRecorder;
import com.github.dreamhead.moco.recorder.StaticRecordHandler;
import com.github.dreamhead.moco.recorder.StaticReplayHandler;

import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoRecorders {
    public static ResponseHandler record(final RequestRecorder recorder) {
        return new StaticRecordHandler(recorder);
    }

    public static ResponseHandler replay(final RequestRecorder recorder) {
        return new StaticReplayHandler(recorder);
    }

    public static ResponseHandler record(final RecorderConfig... configs) {
        RecorderConfigurations configurations = RecorderConfigurations.create(
                checkNotNull(configs, "Configuration should not be null"));
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final RecorderConfig... configs) {
        return new DynamicReplayHandler(RecorderConfigurations.create(
                checkNotNull(configs, "Configuration should not be null")));
    }

    public static RecorderGroup group(final String name) {
        return new RecorderGroup(checkNotNullOrEmpty(name, "Identifier should not be empty"));
    }

    public static RecorderTape tape(final String path) {
        return new RecorderTape(checkNotNullOrEmpty(path, "Identifier should not be empty"));
    }

    public static RecorderIdentifier identifier(final String text) {
        return new RecorderIdentifier(template(checkNotNullOrEmpty(text, "Identifier should not be empty")));
    }

    public static RecorderModifier modifier(final String text) {
        return new RecorderModifier(template(checkNotNullOrEmpty(text, "Identifier should not be empty")));
    }

    private MocoRecorders() {
    }
}
