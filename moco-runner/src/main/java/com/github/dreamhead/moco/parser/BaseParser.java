package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.parser.deserializer.ProxyContainerDeserializer;
import com.github.dreamhead.moco.parser.deserializer.TextContainerDeserializer;
import com.github.dreamhead.moco.parser.model.ProxyContainer;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;

public abstract class BaseParser<T extends Server> implements Parser<T> {
    protected abstract T createServer(ImmutableList<SessionSetting> read, Optional<Integer> port, MocoConfig[] configs);

    protected final CollectionReader reader;

    protected BaseParser() {
        Module textContainerModule = new SimpleModule("TextContainerModule",
                new Version(1, 0, 0, null, null, null))
                .addDeserializer(TextContainer.class, new TextContainerDeserializer());
        Module proxyContainerModule = new SimpleModule("ProxyContainerModule",
                new Version(1, 0, 0, null, null, null))
                .addDeserializer(ProxyContainer.class, new ProxyContainerDeserializer());
        this.reader = new CollectionReader(textContainerModule, proxyContainerModule);
    }

    public T parseServer(InputStream is, Optional<Integer> port, MocoConfig... configs) {
        return createServer(reader.read(is, SessionSetting.class), port, configs);
    }
}
