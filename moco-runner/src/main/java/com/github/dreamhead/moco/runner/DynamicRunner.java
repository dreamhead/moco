package com.github.dreamhead.moco.runner;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class DynamicRunner implements Runner {
    private static Logger logger = LoggerFactory.getLogger(DynamicRunner.class);

    private final FileMonitor fileMonitor = new FileMonitor();
    public final JsonRunner jsonRunner = new JsonRunner();

    private String filename;
    private int port;

    public DynamicRunner(String filename, int port) {
        this.filename = filename;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            jsonRunner.run(new FileInputStream(filename), port);
            fileMonitor.startMonitor(new File(filename), configurationChangeListener(port));
        } catch (FileNotFoundException e) {
            logger.error("failed to find file: {}", filename);
        }
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
                }
            }
        };
    }
}
