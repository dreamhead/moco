package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.DynamicRecordHandler;
import com.github.dreamhead.moco.recorder.DynamicReplayHandler;
import com.github.dreamhead.moco.recorder.FileRecorderFactory;
import com.github.dreamhead.moco.recorder.RecorderRegistry;
import com.github.dreamhead.moco.recorder.RecorderTape;
import com.github.dreamhead.moco.recorder.RequestRecorder;
import com.github.dreamhead.moco.recorder.StaticRecordHandler;
import com.github.dreamhead.moco.recorder.StaticReplayHandler;
import com.github.dreamhead.moco.resource.ContentResource;

import static com.github.dreamhead.moco.recorder.RecorderFactory.IN_MEMORY;

public class MocoRecorders {
    public static ResponseHandler record(final RequestRecorder recorder) {
        return new StaticRecordHandler(recorder);
    }

    public static ResponseHandler replay(final RequestRecorder recorder) {
        return new StaticReplayHandler(recorder);
    }

    public static ResponseHandler record(final String name) {
        return new DynamicRecordHandler(RecorderRegistry.registryOf(name, IN_MEMORY), Moco.text(name));
    }

    public static ResponseHandler replay(final String name) {
        return new DynamicReplayHandler(RecorderRegistry.registryOf(name, IN_MEMORY), Moco.text(name));
    }

    public static ResponseHandler record(final ContentResource name) {
        return new DynamicRecordHandler(RecorderRegistry.defaultRegistry(), name);
    }

    public static ResponseHandler replay(final ContentResource name) {
        return new DynamicReplayHandler(RecorderRegistry.defaultRegistry(), name);
    }

    public static ResponseHandler record(final String groupName, final ContentResource name) {
        return new DynamicRecordHandler(RecorderRegistry.registryOf(groupName, IN_MEMORY), name);
    }

    public static ResponseHandler replay(final String groupName, final ContentResource name) {
        return new DynamicReplayHandler(RecorderRegistry.registryOf(groupName, IN_MEMORY), name);
    }

    public static ResponseHandler record(final String groupName, final RecorderTape tape, final ContentResource name) {
        return new DynamicRecordHandler(RecorderRegistry.registryOf(groupName, new FileRecorderFactory(tape)), name);
    }

    public static ResponseHandler replay(final String groupName, final RecorderTape tape, final ContentResource name) {
        return new DynamicReplayHandler(RecorderRegistry.registryOf(groupName, new FileRecorderFactory(tape)), name);
    }

    public static RecorderTape tape(final String path) {
        return new RecorderTape(path);

    }

    private MocoRecorders() {
    }
}
