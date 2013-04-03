package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.DynamicRunner;
import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.SocketShutdownMonitorRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import static com.github.dreamhead.moco.bootstrap.BootArgs.parse;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int DEFAULT_SHUTDOWN_PORT = 9527;
    private static final String DEFAULT_SHUTDOWN_KEY = "_SHUTDOWN_MOCO_KEY";

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                throw new ParseArgException("at least one arguments required");
            }

            if ("shutdown".equals(args[0])) {
                socketShutdown(DEFAULT_SHUTDOWN_PORT, DEFAULT_SHUTDOWN_KEY);
                System.exit(0);
            }

            BootArgs bootArgs = parse(args);
            Runner runner = new DynamicRunner(bootArgs.getConfigurationFile(), bootArgs.getPort());
            new SocketShutdownMonitorRunner(runner, getShutdownPort(bootArgs), DEFAULT_SHUTDOWN_KEY).run();
        } catch (ParseArgException e) {
            help();
        }
    }

    private static int getShutdownPort(BootArgs bootArgs) {
        return bootArgs.getShutdownPort() == null ? DEFAULT_SHUTDOWN_PORT : bootArgs.getShutdownPort();
    }

    private static void socketShutdown(int shutdownPort, String shutdownMocoKey) {
        try {
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), shutdownPort);
            socket.setSoLinger(false, 0);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write((shutdownMocoKey + "\r\n").getBytes());
            outputStream.flush();
            socket.close();
        } catch (ConnectException e) {
            System.err.println("fail to shutdown, please specify correct shutdown port.");
        } catch (IOException e) {
            logger.error("exception is thrown", e);
            throw new RuntimeException(e);
        }
    }

    private static void help() {
        System.out.println("moco -p port [configuration file]");
        System.exit(1);
    }
}
