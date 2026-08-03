package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.github.dreamhead.moco.util.Jsons;

import java.io.InputStream;
import java.util.List;

public abstract class BaseParser<T extends Server> implements Parser<T> {
    protected abstract T createServer(List<SessionSetting> read,
                                      int port, boolean quiet, MocoConfig... configs);

    public final T parseServer(final List<InputStream> streams,
                               final int port,
                               final boolean quiet,
                               final MocoConfig... configs) {
        List<SessionSetting> settings = Jsons.toObjects(streams, SessionSetting.class);
        return createServer(settings, port, quiet, configs);
    }
}
