package com.github.dreamhead.moco.runner;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class DynamicRunner {
    private static Logger logger = LoggerFactory.getLogger(DynamicRunner.class);

    private final FileMonitor fileMonitor = new FileMonitor();
    public final JsonRunner jsonRunner = new JsonRunner();

    public void run(final String fileName, final int port) {
        try {
            jsonRunner.run(new FileInputStream(fileName), port);
            fileMonitor.startMonitor(new File(fileName), configurationChangeListener(port));
        } catch (FileNotFoundException e) {
            logger.error("failed to find file: {}", fileName);
        }
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

    public void stop() {
        fileMonitor.stopMonitor();
        jsonRunner.stop();
    }
}
