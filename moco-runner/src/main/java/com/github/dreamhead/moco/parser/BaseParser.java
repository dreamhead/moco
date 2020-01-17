package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.toImmutableList;

public abstract class BaseParser<T extends Server> implements Parser<T> {
    protected abstract T createServer(ImmutableList<SessionSetting> read,
                                      int port, MocoConfig... configs);

    public final T parseServer(final ImmutableList<InputStream> streams, final Optional<Integer> port,
                         final MocoConfig... configs) {
        ImmutableList<SessionSetting> settings = Jsons.toObjects(streams, SessionSetting.class);
        ImmutableList<SessionSetting> validSettings = settings.stream()
                .filter(SessionSetting::isValid)
                .collect(toImmutableList());
        return createServer(validSettings, port.orElse(0), configs);
    }
}
