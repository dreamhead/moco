package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.model.MessageContent;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public class Resource implements Identifiable, ConfigApplier<Resource>,
        ResourceReader, ResponseElement, Transformer<Resource, byte[]> {
    private final Identifiable identifiable;
    private final ResourceConfigApplier configApplier;
    private final ResourceReader reader;
    private Function<byte[], byte[]> transformer;

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

        byte[] transformed = transformer.apply(messageContent.getContent());
        return MessageContent.content()
                .withCharset(messageContent.getCharset())
                .withContent(transformed)
                .build();
    }

    @Override
    public Resource transform(final Function<byte[], byte[]> transformer) {
        this.transformer = checkNotNull(transformer, "Transformer should not be null");
        return this;
    }

    public final <T extends ResourceReader> T reader(final Class<T> clazz) {
        return clazz.cast(reader);
    }
}
