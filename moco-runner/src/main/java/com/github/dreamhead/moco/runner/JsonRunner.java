package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class JsonRunner {
    private static Logger logger = LoggerFactory.getLogger(JsonRunner.class);

    private final HttpServerParser httpServerParser = new HttpServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();
    private final FileMonitor fileMonitor = new FileMonitor();

    public void run(final String fileName, final int port) throws IOException {
        run(new FileInputStream(fileName), port);

        fileMonitor.startMonitor(new File(fileName), configurationChangeListener(port));
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

    public void run(InputStream is, int port) throws IOException {
        runner.run(httpServerParser.parseServer(is, port));
    }

    public void stop() {
        fileMonitor.stopMonitor();

        runner.stop();
    }
}
