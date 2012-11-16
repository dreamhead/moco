package com.github.dreamhead.moco.parser.model;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class JsonSetting {
    private int port;
    private List<SessionSetting> sessions = newArrayList();

    public int getPort() {
        return port;
    }

    public List<SessionSetting> getSessions() {
        return sessions;
    }
}
