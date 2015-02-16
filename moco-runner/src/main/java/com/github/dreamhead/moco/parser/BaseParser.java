package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

public abstract class BaseParser<T extends Server> implements Parser<T> {
    protected abstract T createServer(ImmutableList<SessionSetting> read, Optional<Integer> port, MocoConfig[] configs);

    protected final CollectionReader reader;

    protected BaseParser() {
        this.reader = new CollectionReader();
    }

    public T parseServer(InputStream is, Optional<Integer> port, MocoConfig... configs) {
        return createServer(reader.read(is, SessionSetting.class), port, configs);
    }
}
