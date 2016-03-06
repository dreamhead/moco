package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;

import static com.github.dreamhead.moco.parser.model.RestIds.asIdMatcher;
import static com.github.dreamhead.moco.util.Iterables.head;
import static com.github.dreamhead.moco.util.Iterables.tail;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestSubResourceSetting extends ResourceSetting {
    private String id;

    public static Function<RestSubResourceSetting, RestSetting> toSubResourceSetting() {
        return new Function<RestSubResourceSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestSubResourceSetting input) {
                RestSetting[] settings = input.getSettings();

                return MocoRest.id(asIdMatcher(input.id))
                        .name(input.getName()).settings(head(settings), tail(settings));
            }
        };
    }
}
