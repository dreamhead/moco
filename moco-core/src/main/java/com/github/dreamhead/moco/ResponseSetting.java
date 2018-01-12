package com.github.dreamhead.moco;

public interface ResponseSetting<T extends ResponseSetting> extends ResponseBase<T> {
    T on(MocoEventTrigger trigger);
}
