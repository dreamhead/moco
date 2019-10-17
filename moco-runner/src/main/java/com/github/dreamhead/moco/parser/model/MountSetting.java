package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoMount;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.stream.Stream;

import static com.github.dreamhead.moco.MocoMount.exclude;
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
        return Stream.concat(
                includes.stream().map(MocoMount::include),
                excludes.stream().map(MocoMount::exclude))
                .toArray(MountPredicate[]::new);
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
