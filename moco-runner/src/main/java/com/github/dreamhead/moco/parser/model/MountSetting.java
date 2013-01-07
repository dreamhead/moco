package com.github.dreamhead.moco.parser.model;

import java.util.List;

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

    public List<String> getIncludes() {
        return includes;
    }

    public List<String> getExcludes() {
        return excludes;
    }
}
