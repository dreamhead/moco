package com.github.dreamhead.moco.setting;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.internal.SessionContext;

public interface Setting<T extends ResponseSetting> extends ConfigApplier<Setting<T>>, ResponseSetting<T> {
    boolean match(Request request);

    void writeToResponse(SessionContext context);
}
