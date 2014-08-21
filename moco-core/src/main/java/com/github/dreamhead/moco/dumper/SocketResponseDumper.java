package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.Response;

public class SocketResponseDumper implements Dumper<Response> {
    @Override
    public String dump(Response message) {
        return message.getContent();
    }
}
