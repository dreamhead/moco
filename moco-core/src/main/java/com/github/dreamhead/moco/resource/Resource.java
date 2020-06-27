package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.model.MessageContent;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public class Resource implements Identifiable, ConfigApplier<Resource>, ResourceReader, ResponseElement {
    private final Identifiable identifiable;
    private final ResourceConfigApplier configApplier;
    private final ResourceReader reader;
    private Function<MessageContent, MessageContent> transformer;

    public Resource(final Identifiable identifiable,
                    final ResourceConfigApplier configApplier,
                    final ResourceReader reader) {
        this.identifiable = identifiable;
        this.configApplier = configApplier;
        this.reader = reader;
    }

    @Override
    public final Resource apply(final MocoConfig config) {
        return configApplier.apply(config, this);
    }

    @Override
    public final String id() {
        return identifiable.id();
    }

    @Override
    public final MessageContent readFor(final Request request) {
        MessageContent messageContent = reader.readFor(request);
        if (transformer == null) {
            return messageContent;
        }

        return transformer.apply(messageContent);
    }

    public Resource transform(final Function<MessageContent, MessageContent> transformer) {
        this.transformer = checkNotNull(transformer, "Transformer should not be null");
        return this;
    }

    public final <T extends ResourceReader> T reader(final Class<T> clazz) {
        return clazz.cast(reader);
    }
}
