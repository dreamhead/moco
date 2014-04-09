package com.github.dreamhead.moco.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuietMonitor extends AbstractMonitor {
    private static Logger logger = LoggerFactory.getLogger(QuietMonitor.class);

    @Override
    public void onException(final Exception e) {
        logger.error("Exception thrown", e);
    }
}
