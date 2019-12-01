package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.DynamicRecordHandler;
import com.github.dreamhead.moco.recorder.DynamicReplayHandler;
import com.github.dreamhead.moco.recorder.RecorderRegistry;
import com.github.dreamhead.moco.recorder.RequestRecorder;
import com.github.dreamhead.moco.recorder.StaticRecordHandler;
import com.github.dreamhead.moco.recorder.StaticReplayHandler;
import com.github.dreamhead.moco.resource.ContentResource;

public class MocoRecorder {
    public static ResponseHandler record(final RequestRecorder recorder) {
        return new StaticRecordHandler(recorder);
    }

    public static ResponseHandler replay(final RequestRecorder recorder) {
        return new StaticReplayHandler(recorder);
    }

    public static ResponseHandler record(final String name) {
        return new DynamicRecordHandler(RecorderRegistry.registryOf(name), Moco.text(name));
    }

    public static ResponseHandler replay(final String name) {
        return new DynamicReplayHandler(RecorderRegistry.registryOf(name), Moco.text(name));
    }

    public static ResponseHandler record(final ContentResource name) {
        return new DynamicRecordHandler(RecorderRegistry.defaultRegistry(), name);
    }

    public static ResponseHandler replay(final ContentResource name) {
        return new DynamicReplayHandler(RecorderRegistry.defaultRegistry(), name);
    }

    public static ResponseHandler record(final String groupName, final ContentResource name) {
        return new DynamicRecordHandler(RecorderRegistry.registryOf(groupName), name);
    }

    public static ResponseHandler replay(final String groupName, final ContentResource name) {
        return new DynamicReplayHandler(RecorderRegistry.registryOf(groupName), name);
    }

    private MocoRecorder() {
    }
}
