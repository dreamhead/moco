package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;

public class SequenceContentHandler extends AbstractContentResponseHandler {
    private final Resource[] resources;
    private int index;

    public SequenceContentHandler(final Resource[] resources) {
        this.resources = resources;
    }

    @Override
    protected void writeContentResponse(FullHttpRequest request, ByteBuf buffer) {
        buffer.writeBytes(resources[current()].readFor(request));
    }

    private int current() {
        int current = this.index;
        if (++index >= resources.length) {
            index = resources.length - 1;
        }

        return current;
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        FluentIterable<Resource> transformedResources = from(copyOf(resources)).transform(applyConfig(config));
        return new SequenceContentHandler(transformedResources.toArray(Resource.class));
    }

    private Function<Resource, Resource> applyConfig(final MocoConfig config) {
        return new Function<Resource, Resource>() {
            @Override
            public Resource apply(Resource handler) {
                return handler.apply(config);
            }
        };
    }
}
