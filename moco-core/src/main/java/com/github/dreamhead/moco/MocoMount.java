package com.github.dreamhead.moco;

import com.github.dreamhead.moco.mount.MountTo;

public class MocoMount {
    public static MountTo to(String target) {
        return new MountTo(target);
    }
}
