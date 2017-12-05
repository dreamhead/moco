package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.ResponseHandler;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Seqs {
    public static ResponseHandler[] toResponseHandlers(final List<ResponseSetting> settings) {
        List<ResponseHandler> handlers = newArrayList();
        for (ResponseSetting setting : settings) {
            handlers.add(setting.getResponseHandler());
        }

        return handlers.toArray(new ResponseHandler[0]);
    }

    private Seqs() {
    }
}
