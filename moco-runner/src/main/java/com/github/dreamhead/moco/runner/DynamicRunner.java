package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.monitor.FileMonitor;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static com.google.common.collect.ImmutableList.of;

public class DynamicRunner implements Runner {
    private static Logger logger = LoggerFactory.getLogger(DynamicRunner.class);

    private FileMonitor fileMonitor;
    private Runner runner;
    private File file;

    public DynamicRunner(String filename, int port) {
        this.file = new File(filename);
        this.fileMonitor = new FileMonitor(file, configurationChangeListener(port));
        try {
            this.runner = new JsonRunner(of(new FileInputStream(file)), port);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        runner.run();
        fileMonitor.startMonitor();
    }

    @Override
    public void stop() {
        fileMonitor.stopMonitor();
        runner.stop();
    }

    private FileAlterationListener configurationChangeListener(final int port) {
        return new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                logger.info("{} change detected.", file.getName());

                try {
                    restartRunner(port);
                } catch (Exception e) {
                    logger.error("Fail to load configuration in {}.", file.getName());
                    logger.error(e.getMessage());
                }
            }
        };
    }

    private void restartRunner(int port) throws FileNotFoundException {
        runner.stop();
        runner = new JsonRunner(of(new FileInputStream(this.file)), port);
        runner.run();
    }
}
