package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoRest;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.github.dreamhead.moco.parser.model.RestIds.asIdMatcher;
import static com.github.dreamhead.moco.util.Iterables.head;
import static com.github.dreamhead.moco.util.Iterables.tail;
import static com.google.common.collect.FluentIterable.from;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RestSubResourceSetting extends ResourceSetting {
    private String id;

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("id", id);
    }

    private static Function<RestSubResourceSetting, RestSetting> toSubResourceSetting() {
        return new Function<RestSubResourceSetting, RestSetting>() {
            @Override
            public RestSetting apply(final RestSubResourceSetting input) {
                RestSetting[] settings = input.getSettings();

                return MocoRest.id(asIdMatcher(input.id))
                        .name(input.getName()).settings(head(settings), tail(settings));
            }
        };
    }

    public static Iterable<RestSetting> asSubRestSetting(final List<RestSubResourceSetting> setting) {
        if (setting == null || setting.isEmpty()) {
            return ImmutableList.of();
        }

        return from(setting).transform(toSubResourceSetting());
    }
}
