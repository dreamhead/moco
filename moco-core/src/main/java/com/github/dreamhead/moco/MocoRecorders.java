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

    public static ResponseHandler record(final String groupName, final ContentResource identifier) {
        RecorderConfigurations configurations = RecorderConfigurations.create(groupName, new RecorderIdentifier(identifier));
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final String groupName, final ContentResource identifier) {
        RecorderConfigurations configurations = RecorderConfigurations.create(groupName, new RecorderIdentifier(identifier));

        return new DynamicReplayHandler(configurations);
    }

    public static ResponseHandler record(final String groupName,
                                         final RecorderTape tape,
                                         final ContentResource identifier) {
        RecorderConfigurations configurations = RecorderConfigurations.create(groupName, tape, new RecorderIdentifier(identifier));
        return new DynamicRecordHandler(configurations);
    }

    public static ResponseHandler replay(final String groupName,
                                         final RecorderTape tape,
                                         final ContentResource identifier) {
        RecorderConfigurations configurations = RecorderConfigurations.create(groupName, tape, new RecorderIdentifier(identifier));
        return new DynamicReplayHandler(configurations);
    }

    public static ResponseHandler replay(final String groupName,
                                         final ContentResource identifier,
                                         final ContentResource modifier) {
        RecorderConfigurations configurations = RecorderConfigurations.create(groupName, new RecorderIdentifier(identifier),
                new RecorderModifier(modifier));
        return new DynamicReplayHandler(configurations);
    }

    public static RecorderTape tape(final String path) {
        return new RecorderTape(path);
    }

    private MocoRecorders() {
    }
}
