package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventTrigger;
import com.github.dreamhead.moco.util.ToStringHelper;

import static java.util.List.of;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class EventSetting {
    private CompleteEventSetting complete;

    public List<MocoEventTrigger> triggers() {
        if (complete != null) {
            return of(Moco.complete(complete.createTrigger()));
        }

        return of();
    }

    @Override
    public String toString() {
        return ToStringHelper.of(this)
                .omitNullValues()
                .add("complete", complete)
                .toString();
    }
}
