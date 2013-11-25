package com.github.dreamhead.moco;

public class MocoEventTrigger implements ConfigApplier<MocoEventTrigger>{
    private final MocoEvent event;
    private final MocoEventAction action;

    public MocoEventTrigger(MocoEvent event, MocoEventAction action) {
        this.event = event;
        this.action = action;
    }

    public boolean isFor(MocoEvent event) {
        return this.event == event;
    }

    public void fireEvent() {
        action.execute();
    }

    @Override
    public MocoEventTrigger apply(MocoConfig config) {
        MocoEventAction action = this.action.apply(config);
        if (action != this.action) {
            return new MocoEventTrigger(event, action);
        }

        return this;
    }
}
