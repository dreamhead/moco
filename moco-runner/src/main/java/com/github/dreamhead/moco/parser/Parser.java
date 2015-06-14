package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.google.common.base.Optional;

import java.io.InputStream;

public interface Parser<T extends Server> {
    T parseServer(final InputStream is, final Optional<Integer> port, final MocoConfig... configs);
}
