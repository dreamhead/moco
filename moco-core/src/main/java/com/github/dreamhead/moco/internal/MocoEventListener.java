package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.internal.dumper.Dumper;
import com.github.dreamhead.moco.internal.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.internal.dumper.HttpResponseDumper;
import com.google.common.eventbus.Subscribe;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MocoEventListener {
    private Logger logger = LoggerFactory.getLogger(MocoEventListener.class);
    private final Dumper requestDumper = new HttpRequestDumper();
    private final Dumper responseDumper = new HttpResponseDumper();

    @Subscribe
    public void onMessageArrived(HttpRequest request) {
        logger.info("Request received:\n\n{}\n", requestDumper.dump(request));
    }

    @Subscribe
    public void onException(Exception e) {
        logger.error("Exception thrown", e);
    }

    @Subscribe
    public void onMessageLeave(HttpResponse response) {
        logger.info("Response return:\n\n{}\n", responseDumper.dump(response));
    }
}
