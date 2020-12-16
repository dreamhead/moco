package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.recorder.MocoGroup;

public interface SessionGroup {
    void writeAndFlush(Object message, MocoGroup group);

    void join(MocoGroup group);
}
