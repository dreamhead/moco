package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.deserializer.SeqContainerDeserializer;
import com.google.common.base.Function;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

@JsonDeserialize(using = SeqContainerDeserializer.class)
public class SeqContainer implements Container {
    private List<ResponseSetting> seqs;

    public SeqContainer(final List<ResponseSetting> seqs) {
        this.seqs = seqs;
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
