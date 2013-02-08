package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.mount.MountPredicate;

import java.util.List;

import static com.github.dreamhead.moco.MocoMount.exclude;
import static com.github.dreamhead.moco.MocoMount.include;
import static com.google.common.collect.Lists.newArrayList;

public class MountSetting {
    private String dir;
    private String uri;
    private List<String> includes;
    private List<String> excludes;

    public String getDir() {
        return dir;
    }

    public String getUri() {
        return uri;
    }

    public MountPredicate[] getMountPredicates() {
        List<MountPredicate> predicates = newArrayList();

        if (includes != null) {
            for (String include : includes) {
                predicates.add(include(include));
            }
        }

        if (excludes != null) {
            for (String exclude : excludes) {
                predicates.add(exclude(exclude));
            }
        }

        return predicates.toArray(new MountPredicate[0]);
    }
}
