package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import java.util.List;

import static com.github.dreamhead.moco.MocoMount.exclude;
import static com.github.dreamhead.moco.MocoMount.include;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MountSetting extends ResponseSetting {
    private String dir;
    private String uri;
    private List<String> includes = of();
    private List<String> excludes = of();

    public String getDir() {
        return dir;
    }

    public String getUri() {
        return uri;
    }

    public MountPredicate[] getMountPredicates() {
        return toArray(toMountPredicates(), MountPredicate.class);
    }

    private Iterable<MountPredicate> toMountPredicates() {
        return unmodifiableIterable(concat(
                transform(includes, toInclude()),
                transform(excludes, toExclude())));
    }

    private Function<String, MountPredicate> toInclude() {
        return new Function<String, MountPredicate>() {
            @Override
            public MountPredicate apply(String input) {
                return include(input);
            }
        };
    }

    private Function<String, MountPredicate> toExclude() {
        return new Function<String, MountPredicate>() {
            @Override
            public MountPredicate apply(String input) {
                return exclude(input);
            }
        };
    }

    public ResponseHandler getResponseHandler() {
        return this.asResponsSetting().getResponseHandler();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("dir", dir)
                .add("uri", uri)
                .add("includes", includes.isEmpty() ? null : includes)
                .add("excludes", excludes.isEmpty() ? null : excludes)
                .toString();
    }
}
