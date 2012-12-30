package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class JsonRunner {
    private static Logger logger = LoggerFactory.getLogger(JsonRunner.class);

    private final HttpServerParser httpServerParser = new HttpServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();
    private FileAlterationMonitor monitor;

    public void run(final String fileName, final int port) throws IOException {

        run(new FileInputStream(fileName), port);

        monitor = monitorConfigurationFile(fileName, port);
        try {
            monitor.start();
        } catch (Exception e) {
            logger.error("Error found.", e);
        }
    }

    private FileAlterationMonitor monitorConfigurationFile(String fileName, final int port) {
        final File configFile = new File(fileName);
        File parentFile = configFile.getParentFile();
        File directory = (parentFile == null) ? new File(".") : parentFile;
        FileAlterationObserver observer = new FileAlterationObserver(directory, sameFile(configFile));
        observer.addListener(configurationChangeListener(port));

        return new FileAlterationMonitor(1000, observer);
    }

    private FileAlterationListener configurationChangeListener(final int port) {
        return new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                logger.info("{} change detected.", file.getName());

                try {
                    HttpServer httpServer = httpServerParser.parseServer(new FileInputStream(file), port);
                    runner.stop();
                    logger.info("Server is restarting.");
                    runner.run(httpServer);
                } catch (IOException e) {
                    logger.error("Fail to load configuration in {}.", file.getName());
                }
            }
        };
    }

    private FileFilter sameFile(final File configurationFile) {
        return new FileFilter() {
            @Override
            public boolean accept(File detectedFile) {
                return configurationFile.getName().equals(detectedFile.getName());
            }
        };
    }

    public void run(InputStream is, int port) throws IOException {
        runner.run(httpServerParser.parseServer(is, port));
    }

    public void stop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            logger.error("Error found.", e);
        }

        runner.stop();
    }
}
