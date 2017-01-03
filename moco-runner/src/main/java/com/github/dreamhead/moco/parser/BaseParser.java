package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closeables;

import java.io.InputStream;

public abstract class BaseParser<T extends Server> implements Parser<T> {
    protected abstract T createServer(final ImmutableList<SessionSetting> read,
                                      final Optional<Integer> port, final MocoConfig... configs);

    private final CollectionReader reader;

    protected BaseParser() {
        this.reader = new CollectionReader();
    }

    public T parseServer(final InputStream is, final Optional<Integer> port,
                         final MocoConfig... configs) {
        try {
            return createServer(reader.read(is, SessionSetting.class), port, configs);
        } finally {
            Closeables.closeQuietly(is);
        }
    }
}
