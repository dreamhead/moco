package com.github.dreamhead.moco;

public class MocoEventTrigger implements ConfigApplier<MocoEventTrigger> {
    private final MocoEvent event;
    private final MocoEventAction action;

    public MocoEventTrigger(final MocoEvent event, final MocoEventAction action) {
        this.event = event;
        this.action = action;
    }

    public boolean isFor(final MocoEvent event) {
        return this.event == event;
    }

    public void fireEvent() {
        action.execute();
    }

    @Override
    public MocoEventTrigger apply(final MocoConfig config) {
        MocoEventAction appliedAction = this.action.apply(config);
        if (appliedAction != this.action) {
            return new MocoEventTrigger(event, appliedAction);
        }

        return this;
    }
}
