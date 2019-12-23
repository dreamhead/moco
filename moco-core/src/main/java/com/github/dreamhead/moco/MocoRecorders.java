package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.DynamicRecordHandler;
import com.github.dreamhead.moco.recorder.DynamicReplayHandler;
import com.github.dreamhead.moco.recorder.RecorderConfig;
import com.github.dreamhead.moco.recorder.RecorderConfigurations;
import com.github.dreamhead.moco.recorder.RecorderIdentifier;
import com.github.dreamhead.moco.recorder.RecorderModifier;
import com.github.dreamhead.moco.recorder.RecorderTape;
import com.github.dreamhead.moco.recorder.RequestRecorder;
import com.github.dreamhead.moco.recorder.StaticRecordHandler;
import com.github.dreamhead.moco.recorder.StaticReplayHandler;
import com.github.dreamhead.moco.resource.ContentResource;

import static com.github.dreamhead.moco.Moco.template;

public final class MocoRecorders {
    public static ResponseHandler record(final RequestRecorder recorder) {
        return new StaticRecordHandler(recorder);
    }

    public static ResponseHandler replay(final RequestRecorder recorder) {
        return new StaticReplayHandler(recorder);
    }

    public static ResponseHandler record(final String name) {
        RecorderConfigurations configurations = RecorderConfigurations.create(name);
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final String name) {
        RecorderConfigurations configurations = RecorderConfigurations.create(name);
        return new DynamicReplayHandler(configurations);
    }

    public static ResponseHandler record(final ContentResource identifier) {
        RecorderConfigurations configurations = RecorderConfigurations.create(null, new RecorderIdentifier(identifier));
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final ContentResource identifier) {
        RecorderConfigurations configurations = RecorderConfigurations.create(null, new RecorderIdentifier(identifier));
        return new DynamicReplayHandler(configurations);
    }

    public static ResponseHandler record(final RecorderConfig... configs) {
        RecorderConfigurations configurations = RecorderConfigurations.create(null, configs);
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final RecorderConfig... configs) {
        return new DynamicReplayHandler(RecorderConfigurations.create(null, configs));
    }

    public static ResponseHandler record(final String name,
                                         final RecorderConfig... configs) {
        RecorderConfigurations configurations = RecorderConfigurations.create(name, configs);
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final String name,
                                         final RecorderConfig... configs) {
        return new DynamicReplayHandler(RecorderConfigurations.create(name, configs));
    }

    public static RecorderTape tape(final String path) {
        return new RecorderTape(path);
    }

    public static RecorderIdentifier identifier(final String text) {
        return new RecorderIdentifier(template(text));
    }

    public static RecorderModifier modifier(final String text) {
        return new RecorderModifier(template(text));
    }

    private MocoRecorders() {
    }
}
