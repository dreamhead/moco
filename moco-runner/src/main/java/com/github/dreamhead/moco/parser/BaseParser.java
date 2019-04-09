package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

public abstract class BaseParser<T extends Server> implements Parser<T> {
    protected abstract T createServer(ImmutableList<SessionSetting> read,
                                      int port, MocoConfig... configs);

    public final T parseServer(final ImmutableList<InputStream> streams, final Optional<Integer> port,
                         final MocoConfig... configs) {
        ImmutableList<SessionSetting> settings = Jsons.toObjects(streams, SessionSetting.class);
        return createServer(settings, port.or(0), configs);
    }
}
