package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.jboss.netty.buffer.ChannelBuffer;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

public class SequenceContentHandler extends AbstractContentHandler {
    private final Resource[] resources;
    private int index;

    public SequenceContentHandler(final Resource[] resources) {
        this.resources = resources;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(resources[current()].asByteArray());
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
        FluentIterable<Resource> transformedResources = from(newArrayList(resources)).transform(applyConfig(config));
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
