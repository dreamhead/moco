package com.github.dreamhead.moco;

public interface MocoEventAction extends ConfigApplier<MocoEventAction> {
    void execute(Request request);
}
