package com.github.dreamhead.moco.server;

import java.util.Optional;

public interface ServerSetting {
    Optional<Integer> getPort();

    void setPort(int port);
}
