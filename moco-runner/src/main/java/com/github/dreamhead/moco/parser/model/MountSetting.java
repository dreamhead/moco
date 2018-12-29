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
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class MountSetting extends ResponseSetting {
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
        return toArray(concat(
                transform(includes, toInclude()),
                transform(excludes, toExclude())), MountPredicate.class);
    }

    private Function<String, MountPredicate> toInclude() {
        return new Function<String, MountPredicate>() {
            @Override
            public MountPredicate apply(final String input) {
                return include(input);
            }
        };
    }

    private Function<String, MountPredicate> toExclude() {
        return new Function<String, MountPredicate>() {
            @Override
            public MountPredicate apply(final String input) {
                return exclude(input);
            }
        };
    }

    public ResponseHandler getResponseHandler() {
        return this.asResponseSetting().getResponseHandler();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("dir", dir)
                .add("uri", uri)
                .add("includes", toStringList(includes))
                .add("excludes", toStringList(excludes));
    }

    private List<String> toStringList(final List<String> includes) {
        if (includes.isEmpty()) {
            return null;
        }

        return includes;
    }
}
