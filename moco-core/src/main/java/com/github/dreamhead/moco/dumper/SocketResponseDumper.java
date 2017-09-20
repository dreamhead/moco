package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.Response;

public final class SocketResponseDumper implements Dumper<Response> {
    @Override
    public String dump(final Response message) {
        return message.getContent().toString();
    }
}
