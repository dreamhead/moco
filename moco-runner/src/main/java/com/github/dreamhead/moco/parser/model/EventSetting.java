package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.google.common.collect.ImmutableList;

import static com.google.common.collect.ImmutableList.of;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EventSetting {
    private CompleteEventSetting complete;

    public ImmutableList<MocoEventTrigger> createTriggers() {
        if (complete != null) {
            return of(Moco.complete(complete.createTrigger()));
        }

        return of();
    }
}
