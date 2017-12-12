package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.deserializer.SeqContainerDeserializer;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;

@JsonDeserialize(using = SeqContainerDeserializer.class)
public class SeqContainer implements Container {
    private List<ResponseSetting> seqs;

    public SeqContainer(final List<ResponseSetting> seqs) {
        this.seqs = seqs;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("sequence", seqs)
                .toString();
    }

    public ResponseHandler[] toResponseHandlers() {
        return from(seqs).transform(toResponseHandler()).toArray(ResponseHandler.class);
    }

    private Function<ResponseSetting, ResponseHandler> toResponseHandler() {
        return new Function<ResponseSetting, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final ResponseSetting setting) {
                return setting.getResponseHandler();
            }
        };
    }
}
