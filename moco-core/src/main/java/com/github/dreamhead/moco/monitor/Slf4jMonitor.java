package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jMonitor extends AbstractMonitor {
    private static Logger logger = LoggerFactory.getLogger(Slf4jMonitor.class);
    private final Dumper<Request> requestDumper = new HttpRequestDumper();
    private final Dumper<Response> responseDumper = new HttpResponseDumper();

    @Override
    public void onMessageArrived(final Request request) {
        logger.info("Request received:\n\n{}\n", requestDumper.dump(request));
    }

    @Override
    public void onException(final Exception e) {
        logger.error("Exception thrown", e);
    }

    @Override
    public void onMessageLeave(final Response response) {
        logger.info("Response return:\n\n{}\n", responseDumper.dump(response));
    }
}
