package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.MocoSse;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.SseResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.concurrent.TimeUnit;

@JsonDeserialize(using = com.github.dreamhead.moco.parser.deserializer.SseContainerDeserializer.class)
public class SseContainer implements Container {
    private final FileContainer fileContainer;
    private final List<SseEvent> events;
    private final long delay;
    private final TimeUnit unit;

    private SseContainer(final FileContainer fileContainer, final List<SseEvent> events,
                         final long delay, final TimeUnit unit) {
        this.fileContainer = fileContainer;
        this.events = events;
        this.delay = delay;
        this.unit = unit;
    }

    public static SseContainer fromFile(final FileContainer fileContainer, final long delay,
                                        final TimeUnit unit) {
        return new SseContainer(fileContainer, null, delay, unit);
    }

    public static SseContainer fromEvents(final List<SseEvent> events) {
        return new SseContainer(null, events, 0, TimeUnit.MILLISECONDS);
    }

    public static SseContainer fromEvents(final List<SseEvent> events, final long delay,
                                          final TimeUnit unit) {
        return new SseContainer(null, events, delay, unit);
    }

    public final ResponseHandler asResponseHandler() {
        if (fileContainer != null) {
            return applyDelay(MocoSse.sse(toResource()));
        }

        return applyDelay(MocoSse.sse(events.get(0),
                events.subList(1, events.size()).toArray(new SseEvent[0])));
    }

    private ResponseHandler applyDelay(final SseResponseHandler handler) {
        if (delay > 0) {
            return handler.delay(delay, unit);
        }
        return handler;
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
