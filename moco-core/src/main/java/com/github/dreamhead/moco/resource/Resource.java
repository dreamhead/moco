package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.ConfigApplier;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public class Resource implements Identifiable, ConfigApplier<Resource>,
        ResourceReader, ResponseElement, Transformer<byte[]> {
    private final Identifiable identifiable;
    private final ResourceConfigApplier configApplier;
    private ResourceReader reader;

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
        return reader.readFor(request);
    }

    @Override
    public final Transformer<byte[]> transform(final Function<byte[], byte[]> transformer) {
        checkNotNull(transformer, "Transformer should not be null");
        this.reader = new TransformResourceReader(transformer, reader(ContentResourceReader.class));
        return this;
    }

    public final <T extends ResourceReader> T reader(final Class<T> clazz) {
        return clazz.cast(reader);
    }
}
