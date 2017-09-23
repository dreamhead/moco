package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.dumper.Dumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Slf4jMonitor extends AbstractMonitor {
    private static Logger logger = LoggerFactory.getLogger(Slf4jMonitor.class);
    private final Dumper<Request> requestDumper;
    private final Dumper<Response> responseDumper;

    public Slf4jMonitor(final Dumper<Request> requestDumper, final Dumper<Response> responseDumper) {
        this.requestDumper = requestDumper;
        this.responseDumper = responseDumper;
    }

    @Override
    public void onMessageArrived(final Request request) {
        logger.info("Request received:\n\n{}\n", requestDumper.dump(request));
    }

    @Override
    public void onException(final Throwable t) {
        logger.error("Exception thrown", t);
    }

    @Override
    public void onMessageLeave(final Response response) {
        logger.info("Response return:\n\n{}\n", responseDumper.dump(response));
    }
}
