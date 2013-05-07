package com.github.dreamhead.moco.runner;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static com.google.common.collect.ImmutableList.of;

public class DynamicRunner implements Runner {
    private static Logger logger = LoggerFactory.getLogger(DynamicRunner.class);

    private final FileMonitor fileMonitor = new FileMonitor();
    public final JsonRunner jsonRunner;

    private String filename;
    private int port;

    public DynamicRunner(String filename, int port) {
        this.filename = filename;
        this.port = port;
        try {
            this.jsonRunner = new JsonRunner(of(new FileInputStream(filename)), port);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        jsonRunner.run();
        fileMonitor.startMonitor(new File(filename), configurationChangeListener(port));
    }

    @Override
    public void stop() {
        fileMonitor.stopMonitor();
        jsonRunner.stop();
    }

    private FileAlterationListener configurationChangeListener(final int port) {
        return new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                logger.info("{} change detected.", file.getName());

                try {
                    jsonRunner.restart(new FileInputStream(file), port);
                } catch (Exception e) {
                    logger.error("Fail to load configuration in {}.", file.getName());
                    logger.error(e.getMessage());
                }
            }
        };
    }
}
