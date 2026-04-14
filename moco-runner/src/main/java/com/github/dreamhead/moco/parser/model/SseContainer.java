package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.MocoSse;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.with;

@JsonDeserialize(using = com.github.dreamhead.moco.parser.deserializer.SseContainerDeserializer.class)
public class SseContainer implements Container {
    private final FileContainer fileContainer;
    private final List<SseEvent> events;

    private SseContainer(final FileContainer fileContainer, final List<SseEvent> events) {
        this.fileContainer = fileContainer;
        this.events = events;
    }

    public static SseContainer fromFile(final FileContainer fileContainer) {
        return new SseContainer(fileContainer, null);
    }

    public static SseContainer fromEvents(final List<SseEvent> events) {
        return new SseContainer(null, events);
    }

    public final ResponseHandler asResponseHandler() {
        if (fileContainer != null) {
            return MocoSse.sse(toResource());
        }

        return MocoSse.sse(events.get(0),
                events.subList(1, events.size()).toArray(new SseEvent[0]));
    }

    private Resource toResource() {
        return DynamicResponseHandlerFactory.asFileResource("file", fileContainer);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("file", fileContainer)
                .add("events", events)
                .toString();
    }
}
