package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventAction;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CompleteEventSetting {
    private boolean async;
    private long latency;
    private PostSetting post;
    private GetSetting get;

    public MocoEventAction createTrigger() {
        MocoEventAction action = doCreateAction();

        if (this.async) {
            return Moco.async(action, Moco.latency(latency));
        }

        return action;

    }

    private MocoEventAction doCreateAction() {
        if (get != null) {
            return get.createAction();
        }

        if (post != null) {
            return post.createAction();
        }

        return null;
    }
}
